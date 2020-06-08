package fr.jamailun.halystia.quests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonManager;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.npcs.traits.HalystiaRpgTrait;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepBring;
import fr.jamailun.halystia.quests.steps.QuestStepDonjon;
import fr.jamailun.halystia.quests.steps.QuestStepInteract;
import fr.jamailun.halystia.quests.steps.QuestStepKill;
import fr.jamailun.halystia.quests.steps.QuestStepSpeak;

public class QuestManager {

	private final Set<Quest> quests;
	private final String path;
	private final HalystiaRPG main;
	private final NpcManager npcs;
	private final MobManager mobs;
	private final DonjonManager donjons;
	
	public QuestManager(String path, HalystiaRPG main, NpcManager npcs, MobManager mobs, DonjonManager donjons) {
		this.path = path;
		this.main = main;
		this.npcs = npcs;
		this.mobs = mobs;
		this.donjons = donjons;
		quests = new HashSet<>();
		reload();
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
	
	public Quest createQuest(String idName) {
		if(getQuestById(idName) != null)
			return null;
		Quest quest = new Quest(path, idName, main, npcs, mobs, donjons);
		quests.add(quest);
		return quest;
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
		quest.deleteData();
		quests.remove(quest);
	}
	
	public void reload() {
		quests.clear();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				quests.add(new Quest(path, name, main, npcs, mobs, donjons));
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
					
				}
			}
		}
	}
}