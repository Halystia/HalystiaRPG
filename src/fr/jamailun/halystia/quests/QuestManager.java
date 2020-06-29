package fr.jamailun.halystia.quests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonManager;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.npcs.traits.HalystiaRpgTrait;
import fr.jamailun.halystia.quests.players.QuestState;
import fr.jamailun.halystia.quests.players.QuestState.QuestStatus;
import fr.jamailun.halystia.quests.players.QuestsAdvancement;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepBring;
import fr.jamailun.halystia.quests.steps.QuestStepDonjon;
import fr.jamailun.halystia.quests.steps.QuestStepInteract;
import fr.jamailun.halystia.quests.steps.QuestStepKill;
import fr.jamailun.halystia.quests.steps.QuestStepSpeak;
import fr.jamailun.halystia.sql.temporary.DataHandler;

public class QuestManager {

	private final Set<Quest> quests;
	private final String path;
	private final HalystiaRPG main;
	private final NpcManager npcs;
	private final MobManager mobs;
	private final DonjonManager donjons;
	
	private Set<QuestsAdvancement> playersAdvancements;
	
	public QuestManager(String path, HalystiaRPG main, NpcManager npcs, MobManager mobs, DonjonManager donjons, DataHandler bdd) {
		this.path = path;
		this.main = main;
		this.npcs = npcs;
		this.mobs = mobs;
		this.donjons = donjons;
		quests = new HashSet<>();
		
		reload();
		
		playersAdvancements = new HashSet<>();
	}
	
	public boolean hasDataAbout(Player player) {
		return playersAdvancements.stream().anyMatch(adv -> adv.owns(player));
	}
	
	public QuestsAdvancement generateDataAbout(Player player) {
		if(hasDataAbout(player))
			return getPlayerData(player);
		Set<QuestState> states = new HashSet<>();
		for(Quest quest : quests) {
			int step = main.getDataBase().getStepInQuest(player, quest);
			if(step > -1) {
				if(step >= quest.getHowManySteps()) {
					states.add(new QuestState(quest, step, 0, QuestStatus.FINISHED));
					continue;
				}
				int data = main.getDataBase().getDataInQuest(player, quest);
				states.add(new QuestState(quest, step, data, QuestStatus.STARTED));
				continue;
			}
			states.add(new QuestState(quest, -1, 0, QuestStatus.NOT_STARTED));
		}
		QuestsAdvancement adv = new QuestsAdvancement(player.getUniqueId(), states);
		playersAdvancements.add(adv);
		return adv;
	}
	
	public Set<Quest> getAllQuests() {
		return new HashSet<>(quests);
	}
	
	public Stream<String> getAllConfigIdsStream() {
		return quests.stream().map(q -> q.getID());
	}
	
	public Quest getQuestById(String id) {
		try {
			return quests.stream().filter(q -> q.getID().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public Quest createQuest(String idName, RpgNpc npc) {
		if(getQuestById(idName) != null)
			return null;
		Quest quest = new Quest(path, npc, idName, main, npcs, mobs, donjons);
		quests.add(quest);
		
		for(QuestsAdvancement adv : playersAdvancements) {
			adv.questAdded(new QuestState(quest, 0, 0, QuestStatus.NOT_STARTED));
		}
		
		return quest;
	}
	
	public Set<Quest> getQuestsStartedByNPC(RpgNpc npc) {
		return quests.stream().filter(q -> q.getNPCId().equals(npc.getConfigId())).collect(Collectors.toSet());
	}
	
	public void removeQuest(Quest quest) {
		for(RpgNpc npc : npcs.getNpcs()) {
			if( ! npc.getNPC().hasTrait(HalystiaRpgTrait.class) ) {
				main.getConsole().sendMessage(ChatColor.RED + "Attention ! Le NPC (id="+npc.getConfigId()+") n'as pas le trait rpg !");
				continue;
			}
			HalystiaRpgTrait trait = npc.getNPC().getTrait(HalystiaRpgTrait.class);
			if( ! trait.hasQuest() )
				continue;
			if(trait.getQuestName().equals(quest.getID())) {
				trait.resetQuest();
				main.getConsole().sendMessage(ChatColor.GREEN + "Le NPC (id="+npc.getConfigId()+") était porteur de la quete '"+quest.getID()+"'. Il en a été libéré.");
				break;
			}
		}
		
		for(QuestsAdvancement adv : playersAdvancements) {
			adv.questRemoved(quest.getID());
		}
		
		quest.deleteData();
		quests.remove(quest);
	}
	
	public void reload() {
		quests.clear();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				quests.add(new Quest(path, null, name, main, npcs, mobs, donjons));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void verifyNpcs(NpcManager npcMgr, MobManager mobs) {
		Set<RpgNpc> npcs = npcMgr.getNpcs();
		for(Quest quest : quests) {
			int st = -1;
			for(QuestStep step : quest.getSteps()) {
				st++;
				if( step instanceof QuestStepSpeak ) {
					if( ! npcs.contains(((QuestStepSpeak)step).getTarget())) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le step de la quete ("+quest.getID()+") numéro " + st + " appelle un NPC non valide.");
						continue;
					}
				} else if( step instanceof QuestStepBring ) {
					if( ! npcs.contains(((QuestStepBring)step).getTarget())) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le step de la quete ("+quest.getID()+") numéro " + st + " appelle un NPC non valide.");
						continue;
					}
				} else if( step instanceof QuestStepKill ) {
					if( ! mobs.getAllMobNames().contains(((QuestStepKill)step).getMobName())) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le step de la quete ("+quest.getID()+") numéro " + st + " appelle un monstre non valide.");
						continue;
					}
				} else if( step instanceof QuestStepDonjon ) {
					if( main.getDonjonManager().getLegacyWithConfigName(((QuestStepDonjon)step).getDonjonID()) == null) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le step de la quete ("+quest.getID()+") numéro " + st + " appelle un donjon non valide.");
						continue;
					}
				} else if( step instanceof QuestStepInteract ) {
					// ?
				}
			}
		}
	}

	public QuestsAdvancement getPlayerData(Player player) {
		try {
			return playersAdvancements.stream().filter(pl -> pl.owns(player)).findAny().get();
		} catch ( NoSuchElementException e) {
//			Bukkit.broadcastMessage("§4§lrien trouvé : on regénère.");
			return generateDataAbout(player);
		}
	}
}