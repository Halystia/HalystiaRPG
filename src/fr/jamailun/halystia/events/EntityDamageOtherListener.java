package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierBrut;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierPrecis;
import fr.jamailun.halystia.spells.newSpells.epeiste.Damocles;
import fr.jamailun.halystia.spells.spellEntity.InvocationsManager;
import net.citizensnpcs.api.CitizensAPI;

public class EntityDamageOtherListener extends HalystiaListener {

	private final InvocationsManager invocs;
	
	public EntityDamageOtherListener(HalystiaRPG main) {
		super(main);
		invocs = main.getSpellManager().getInvocationsManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDamageEntity(EntityDamageByEntityEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getDamager()))
			return;
		if(main.getSuperMobManager().damageMob(e.getEntity(), e.getDamager().getUniqueId(), 0)) {
			e.setCancelled(true);
		}
		if( ! (e.getEntity() instanceof LivingEntity))
			return;
		
		
		// Si l'entité qui attaque est un EnemyMob
		if(main.getMobManager().hasMob(e.getDamager().getEntityId())) {
			EnemyMob mob = main.getMobManager().getWithEntityId(e.getDamager().getEntityId());
			double customDamages = mob.getCustomDamages();
			if(customDamages > -1)
				e.setDamage(customDamages);
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity target = (LivingEntity) e.getEntity();
				if(mob.isPoisonous())
					target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 1));
				if(mob.isWitherous())
					target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*5, 1));
			}
			if(e.getEntity() instanceof Player && ((LivingEntity)e.getEntity()).getHealth() < e.getDamage()) {
				alertDeathPlayer(e.getEntity().getName(), mob.getCustomName());
			}
			return;
		}
		
		// Si l'entité qui attaque est une invocation
		if(invocs.contains(e.getDamager().getUniqueId())) {
			Entity damager = e.getDamager();
			
			//MàJ des dommages
			double newDamages = invocs.getDamages(damager);
			if(newDamages != -1)
				e.setDamage(newDamages);
			
			//On annule si jamais c'est le créateur !
			if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				
				if(invocs.isMasterOf(p, damager)) {
					if(damager instanceof Arrow)
						damager.remove();
					e.setCancelled(true);
					return;
				}
				
				if( ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
					if(((LivingEntity)e.getEntity()).getHealth() < e.getDamage()) {
						alertDeathPlayer(e.getEntity().getName(), damager.getCustomName() != null ? damager.getCustomName() : invocs.getCasterName(damager));
					}
				}
			}
			
			if(main.getMobManager().hasMob(e.getEntity().getEntityId())) {
				LivingEntity liv = (LivingEntity) e.getEntity();
				if(liv.getHealth() - e.getDamage() <= 0) {
					//L'invocation tue une saloperie ! Et elle tue un enemymob !
					
					
					return;
				}
			}
			
			
		}
		if(CitizensAPI.getNPCRegistry().isNPC(e.getDamager())) {
			if(e.getEntity() instanceof Player && ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
				if(((LivingEntity)e.getEntity()).getHealth() < e.getDamage()) {
					alertDeathPlayer(e.getEntity().getName(), CitizensAPI.getNPCRegistry().getNPC(e.getDamager()).getName());
				}
			}
			return;
		}
		
		
		
		// Si l'entité qui attaque c'est un Player
		if( ! (e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();
		if(e.getEntity() instanceof Player && ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
			if(((LivingEntity)e.getEntity()).getHealth() < e.getDamage()) {
				alertDeathPlayer(e.getEntity().getName(), p.getName());
			}
		}
		
		if(Damocles.damoclers.contains(p.getUniqueId())) {
			e.setDamage(e.getDamage() * 2);
			p.damage(Damocles.DAMAGES);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, .6f, .8f);
			if(e.getEntity() instanceof Player)
				((Player)e.getEntity()).playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 2f, .8f);
		}
		if(AcierPrecis.ralentisseurs.contains(p.getUniqueId())) {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, .5f);
			((LivingEntity)e.getEntity()).addPotionEffect(AcierPrecis.effect);
		}
		if(AcierBrut.empoisoners.contains(p.getUniqueId())) {
			p.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_SPIDER, 1f, .5f);
			for(PotionEffect effect : AcierBrut.effects)
				((LivingEntity)e.getEntity()).addPotionEffect(effect);
		}
		
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		Classe classe = main.getClasseManager().getPlayerData(p).getClasse();
		if(p.getInventory().getItemInMainHand() != null) {
			Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInMainHand());
			if(classe != ob && ob != Classe.NONE) {
				e.setCancelled(true);
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
				return;
			}
		}
		
		if(p.getInventory().getItemInOffHand() != null) {
			Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInOffHand());
			if(classe != ob && ob != Classe.NONE) {
				e.setCancelled(true);
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
				return;
			}
		}
		
		if(main.getSuperMobManager().damageMob(e.getEntity(), p.getUniqueId(), e.getDamage())) {
			e.setCancelled(true);
			return;
		}
		
	}
	private final static String SKULL = new String(Character.toChars(10060));
	
	public static void alertDeathPlayer(String killed, String killer) {
		String message = ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + killed + ChatColor.GRAY;
		if(killer == null) {
			Bukkit.broadcastMessage(message + " s'est suicidé.");
		} else {
			Bukkit.broadcastMessage(message + " a été tué par " + ChatColor.RED + killer + ChatColor.GRAY + ".");
		}
	}
	
	@EventHandler
	public void entityDamaged(EntityDamageEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if(main.getMobManager().hasMob(e.getEntity().getEntityId())) {
			EnemyMob mob = main.getMobManager().getWithEntityId(e.getEntity().getEntityId());
			if(mob.doesResistFire()) {
				if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA || e.getCause() == DamageCause.LIGHTNING || e.getCause() == DamageCause.CRAMMING) {
					e.getEntity().setFireTicks(0);
					e.setCancelled(true);
					return;
				}
			}
		}
	}
}