package fr.jamailun.halystia.npcs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.ExclamationManagement.ExclamationType;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.players.QuestsAdvancement;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepBring;
import fr.jamailun.halystia.quests.steps.QuestStepSpeak;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;

public final class NpcManager {
	public final static long TIME_BETWEEN_MESSAGES = 35L;
	
	private final Set<RpgNpc> npcs;
	private final String path;
	private final Map<String, Texture> textures;
	private final HalystiaRPG main;
	private final File textureFile;
	
	private RpgNpc generateImplementation(String path, String name, Location shouldSpawn) {
		return new CitizenNpc2(path, name, shouldSpawn);
	}
	
	public NpcManager(String path, String texturesFile, HalystiaRPG main) {
		this.main = main;
		this.path = path;
		this.textureFile = new File(texturesFile);
		if( ! textureFile.exists())
			System.err.println("LE FICHIER DE TEXTURES N'EXISTE PAS !!!");
		npcs = new HashSet<>();
		textures = new HashMap<>();
		
		reload(); //appelle le reloadTextures() :D
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
					try {
						npc.spawn(npc.getStoredLocation(), SpawnReason.CREATE);
					} catch (NullPointerException ee) {
						main.getConsole().sendMessage(ChatColor.RED+"Impossible de spawner le npc id="+npc.getId()+".");
						continue;
					}
		            RpgNpc rpg = getNpc(npc);
		            if(rpg != null)
		            	if(rpg instanceof CitizenNpc2 && rpg.getEntityId() == npc.getId())
		            		((CitizenNpc2)rpg).updateNpc(npc);
		            try {
		            	npc.teleport(npc.getStoredLocation(), TeleportCause.PLUGIN);
		            } catch (Exception e) {
		            	System.out.println("Impossible de teleporter npc " + npc.getId()+".");
		            }
		        }
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Completely reloaded HalystiaRPG and npcs.");
				startRotateAllExclamations();
			}
		}.runTaskLater(main, 100L);
		
		clockToDoThings();
	}
	
	private void startRotateAllExclamations() {
		new BukkitRunnable() {
			@Override
			public void run() {
				npcs.forEach(npc -> npc.getExclamation().rotate());
			}
		}.runTaskTimer(main, 100L, 4L);
	}
	
	public void reloadTextures() {
		textures.clear();
		FileConfiguration config = YamlConfiguration.loadConfiguration(textureFile);
		for(String key : config.getKeys(false))
			if(config.contains(key+".texture") && config.contains(key+".signature"))
				textures.put(key.toLowerCase(), new Texture(config.getConfigurationSection(key)));
	}
	
	public RpgNpc getNpcWithConfigId(String id) {
		try {
			return npcs.stream().filter(npc -> npc.getConfigId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public RpgNpc getNpcWithUUID(UUID uuid) {
		try {
			return npcs.stream().filter(npc -> npc.getUUID().equals(uuid)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public void refreshExclamations(Player player) {
		int llevel = 0;
		try {
			llevel = main.getClasseManager().getPlayerData(player).getLevel();
		} catch(Exception e) {
			return;
		}
		final int level = llevel;
		QuestsAdvancement adv = main.getQuestManager().getPlayerData(player);
		allnpcs: for(RpgNpc npc : npcs) {
			try {
				if(player.getLocation().distance(npc.getLocation()) > 60) {
					npc.getExclamation().purge(player);
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			if( ! (npc instanceof CitizenNpc2))
				continue;
			ExclamationManagement excl = npc.getExclamation();
			// déjà, étapes où il doit faire un rapport
			for(QuestStep step : adv.getOnGoingQuestSteps()) {
				//System.out.println("quest ("+step.getQuest().getID()+" : step n°"+step.getStep()+" type : " + step.getType()+".");
				if(step instanceof QuestStepBring) {
					QuestStepBring realStep = (QuestStepBring) step;
					if(realStep.getTarget().equals(npc)) {
						excl.changeState(player, ExclamationType.QUEST_REPORT);
						continue allnpcs;
					}
				}
				if(step instanceof QuestStepSpeak) {
					QuestStepSpeak realStep = (QuestStepSpeak) step;
				//	System.out.println("target="+realStep.getTarget()+", ici npc="+npc);
					if(realStep.getTarget().equals(npc)) {
						excl.changeState(player, ExclamationType.QUEST_REPORT);
						continue allnpcs;
					}
				}
			}
			
			//Ensuite, si le npc commence une quete
			Set<Quest> starting = main.getQuestManager().getQuestsStartedByNPC(npc);
			starting.removeIf(q -> adv.knows(q));
			if(starting.isEmpty()) {
				excl.changeState(player, ExclamationType.NONE);
				continue;
			}
			starting.removeIf(q -> q.getLevel() > level);
			if(starting.isEmpty()) {
				excl.changeState(player, ExclamationType.NOT_LEVEL);
				continue;
			}
			excl.changeState(player, ExclamationType.QUEST_POSSIBLE);
		}
	}
	
	public Set<String> getAllConfigIds() {
		return npcs.stream().map(npc -> npc.getConfigId()).collect(Collectors.toSet());
	}
	
	public RpgNpc createNpc(String idName, Location location) {
		if(getNpcWithConfigId(idName) != null)
			return null;
		RpgNpc npc = generateImplementation(path, idName, location);
		npcs.add(npc);
		return npc;
	}
	
	public Set<RpgNpc> getNpcs() {
		return new HashSet<>(this.npcs);
	}

	public boolean removeNpc(RpgNpc npc) {
		npc.deleteData();
		npcs.remove(npc);
		return true;
	}

	public void reload() {
		reloadTextures();
		npcs.clear();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				npcs.add(generateImplementation(path, name, null));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<String> getTextures() {
		return textures.keySet();
	}
	
	public Texture getTexture(String skin) {
		return textures.get(skin);
	}

	public RpgNpc getNpc(NPC npc) {
		for(RpgNpc cnpc : npcs) {
			if(npc.getId() == cnpc.getEntityId()) {
				return cnpc;
			}
		}
		return null;
	}
	
	private void clockToDoThings() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(RpgNpc npc : npcs) {
					if( ! npc.isValid())
						continue;
					if( ! npc.getNPC().isSpawned())
						continue;
					if(npc.getMode() == NpcMode.SENTINEL) {
						SentinelTrait trait = npc.getNPC().getTrait(SentinelTrait.class);
						if( ! npc.getNPC().getNavigator().isNavigating()) {
							trait.pathTo(trait.spawnPoint);
						}
					}
				}
				
			}
		}.runTaskTimer(main, 10*20L, 10*20L);
	}

	public void purgeExclamations() {
		npcs.stream().filter(r -> r instanceof CitizenNpc2).map(r -> (CitizenNpc2)r).forEach(n -> n.getExclamation().purge());
	}

	public void purgeExclamations(Player player) {
		npcs.stream().filter(r -> r instanceof CitizenNpc2).map(r -> (CitizenNpc2)r).forEach(n -> n.getExclamation().purge(player));
	}
}