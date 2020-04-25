package fr.jamailun.halystia.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepKill;
import fr.jamailun.halystia.spells.spellEntity.InvocationsManager;
import net.citizensnpcs.api.CitizensAPI;

public class MobDeathListener extends HalystiaListener {

	private final InvocationsManager invocs;
	
	public MobDeathListener(HalystiaRPG main) {
		super(main);
		invocs = main.getSpellManager().getInvocationsManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void creatureDeathEvent(EntityDeathEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		e.setDroppedExp(0);
		if(invocs.contains(e.getEntity().getUniqueId())) {
			e.getDrops().clear();
			invocs.entityDeath(e.getEntity());
			return;
		}
		if( ! main.getMobManager().hasMob(e.getEntity().getEntityId()) )
			return;
		
		EnemyMob mob = main.getMobManager().getWithEntityId(e.getEntity().getEntityId());
		
		e.getDrops().clear();
		for(ItemStack drop : mob.getLoots())
			Bukkit.getServer().getWorld(HalystiaRPG.WORLD).dropItemNaturally(e.getEntity().getLocation(), drop);
		
		if(e.getEntity().getKiller() == null)
			return;
		if(CitizensAPI.getNPCRegistry().isNPC(e.getEntity().getKiller())) {
			/*
			 * RpgNpc npc = main.getNpcManager().getNpc(CitizensAPI.getNPCRegistry().getNPC(e.getEntity().getKiller()));
			if(npc == null || npc.getNPC() == null)
				return;
			SentinelTrait trait = npc.getNPC().getTrait(SentinelTrait.class);
			if(trait.chasing == null && npc.getNPC().getEntity().getLocation().distance(npc.getLocation()) > .1) {
				trait.pathTo(npc.getLocation());
			}
			*/
			return;
		}
		PlayerData pc = main.getClasseManager().getPlayerData(((Player)e.getEntity().getKiller()));
		if(pc != null) {
			pc.addXp(mob.getXp());
			for(QuestStep questStep : main.getDataBase().getOnGoingQuestSteps(pc.getPlayer())) {
				if(questStep instanceof QuestStepKill) {
					QuestStepKill step = (QuestStepKill) questStep;
					if(step.getMobName().equals(mob.getConfigName()))
						step.playerKillOneMob(pc.getPlayer());
				}
			}
		}
	}
	
}