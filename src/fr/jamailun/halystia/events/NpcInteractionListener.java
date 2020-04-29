package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepBring;
import fr.jamailun.halystia.quests.steps.QuestStepSpeak;
import fr.jamailun.halystia.shops.Shop;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NpcInteractionListener extends HalystiaListener {

	private List<Player> actives;
	
	public NpcInteractionListener(HalystiaRPG main) {
		super(main);
		actives = new ArrayList<Player>();
	}
	
	@EventHandler
	public void citizenInteract(NPCRightClickEvent e) {
		RpgNpc npc = main.getNpcManager().getNpc(e.getNPC());
		Player p = e.getClicker();
		if(npc == null) {
			if(p.isOp())
				p.sendMessage(DARK_RED + "(op only) -> NPC invalide ! Merci de le supprimer avec les commandes de citizens ? sauf si c'est fait exprès xD");
			return;
		}
		//1 : valider le step !
		for(QuestStep step : main.getDataBase().getOnGoingQuestSteps(p)) {
			if(step instanceof QuestStepSpeak) {
				QuestStepSpeak realStep = (QuestStepSpeak) step;
				if(realStep.getTarget().equals(npc)) {
					realStep.valid(p);
					return;
				}
			}
			if(step instanceof QuestStepBring) {
				QuestStepBring realStep = (QuestStepBring) step;
				if(realStep.getTarget().equals(npc)) {
					realStep.trade(p);
					return;
				}
			}
		}
		
	//	System.out.println("no validation");
		
		//2 : si aucune quête ne part du NPC, on peut faire le dialogue normal.
		if( ! npc.hasQuest()) {
			npc.speak(p);
			return;
		}
		
	//	System.out.println(" quete");
		
		Quest quest = main.getQuestManager().getQuestById(npc.getQuestName());
		if(quest == null) {
			p.sendMessage(RED + "Erreur ! La quête débutée par ce NPC est nulle !");
			npc.speak(p);
			return;
		}
		
	//	System.out.println("quest="+quest.getID());
		
		if( ! quest.isvalid()) {
			npc.speak(p);
			return;
		}
		if( ! quest.isCorrect()) {
			System.err.println("QUETE INVALIDE ("+quest.getID()+").");
			npc.speak(p);
			return;
		}
		// 3 : Quête non terminée : mais toujours truc
		if(main.getDataBase().getOnGoingQuests(p).contains(quest)) {
			npc.speak(p);
			return;
		}

	//	System.out.println("quete non commencée");
		
		//Quête non commencée : on la commence :D
		if(quest.playerHasLevel(p)) {
	//		System.out.println("niveau validé !");
			int size = quest.sendIntroduction(npc, p);
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				public void run() {
					quest.startQuest(p);
					npc.free(p);
				}
			}, (NpcManager.TIME_BETWEEN_MESSAGES + 2L) * size );
			return;
		} else {
	//		System.out.println("niveau insuffisant !");
	//		npc.sendMessage(p, ChatColor.RED + "Tu");
	//		npc.free(p);
			npc.speak(p);
			return;
		}
		//tous les cas ont été explorés !
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerInteractVillager(PlayerInteractAtEntityEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
				return;
		
		Player p = e.getPlayer();
		
		p.sendMessage("CONNU ? " + (main.getMobManager().getWithEntityId(e.getRightClicked().getEntityId())!=null) );
		
		
		
		if(actives.contains(p)) {
			e.setCancelled(true);
			return;
		}
		actives.add(p);
		scheduleRemove(p);
		
		Shop shop = main.getShopManager().getShop(e.getRightClicked().getUniqueId());
		if(shop == null)
			return;
		
		e.setCancelled(true);
		
		if(p.isSneaking() && p.isOp()) {
			shop.openParametersGUI(p);
			return;
		}
		
		PlayerData pc = main.getClasseManager().getPlayerData(p);
		if(pc == null)
			return;
		if(pc.getClasse()!= shop.getClasse()) {
			p.sendMessage(
					HalystiaRPG.PREFIX + RED + "Tu n'as pas la bonne classe pour accéder à ces articles ! "
					+ ((pc.getClasse() == Classe.NONE) ? 
						"Tu n'as actuellement aucune classe..."
						: "Tu es " + DARK_RED + pc.getClasse().getName() + RED + ", ce PNJ est " + DARK_RED + shop.getClasse().getName() + RED + "."));
			return;
		}
		shop.openGUI(pc);
	}
	
	private void scheduleRemove(Player p) {
		Bukkit.getScheduler().runTaskLater(main, new Runnable() {
			public void run() {
				try {
					actives.remove(p);
				} catch(Exception e) {
					scheduleRemove(p);
				}
			}
		}, 20L);
	}

}
