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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.quests.QuestManager;
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
		            npc.spawn(npc.getStoredLocation(), SpawnReason.CREATE);
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
			}
		}.runTaskLater(main, 100L);
		
		clockToDoThings();
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
	
	public void verifyQuests(QuestManager quests) {
		for(RpgNpc npc : npcs) {
			if(npc.hasQuest()) {
				if( ! quests.getAllConfigIdsStream().collect(Collectors.toList()).contains(npc.getQuestName())) {
					npc.changeQuest(null);
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le npc ["+npc.getConfigId()+"] a vu sa quete se faire reset car elle n'existait plus.");
				}
			}
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
}