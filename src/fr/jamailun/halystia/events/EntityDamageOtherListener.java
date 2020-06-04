package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
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
import fr.jamailun.halystia.custom.PlayerEffectsManager;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierBrut;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierPrecis;
import fr.jamailun.halystia.spells.newSpells.epeiste.Damocles;
import fr.jamailun.halystia.spells.spellEntity.InvocationsManager;
import net.citizensnpcs.api.CitizensAPI;

public class EntityDamageOtherListener extends HalystiaListener {

	private final InvocationsManager invocs;
	private final PlayerEffectsManager effects;
	
	public EntityDamageOtherListener(HalystiaRPG main) {
		super(main);
		invocs = main.getSpellManager().getInvocationsManager();
		effects = main.getPlayerEffectsManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDamageEntity(EntityDamageByEntityEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getDamager()))
			return;
		if (e.getEntity() instanceof ItemFrame) {
			e.setCancelled(true);
			if(e.getDamager() instanceof Player)
				if(((Player)e.getDamager()).getGameMode() == GameMode.CREATIVE)
					e.setCancelled(false);
			return;
        }
		
		if(main.getSuperMobManager().damageMob(e.getEntity(), e.getDamager().getUniqueId(), 0)) {
			e.setCancelled(true);
		}
		if(main.getDonjonManager().getBossManager().isBoss(e.getDamager())) {
			if(e.getEntity() instanceof Player && ((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
				alertDeathPlayer((Player)e.getEntity(), "le "+main.getDonjonManager().getBossManager().getBoss(e.getDamager()).getCustomName());
			}
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
			if(main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), e.getDamager().getUniqueId(), e.getDamage())) {
				e.setCancelled(true);
				return;
			}
			if(e.getEntity() instanceof Player && ((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
				alertDeathPlayer((Player)e.getEntity(), "un "+mob.getCustomName());
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
			
			if(main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), invocs.getMasterOf(damager.getUniqueId()), e.getDamage())) {
				e.setCancelled(true);
				return;
			}
			
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
					if(((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
						alertDeathPlayer((Player)e.getEntity(), "un "+ damager.getCustomName() != null ? damager.getCustomName() : invocs.getCasterName(damager));
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
				if(((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
					alertDeathPlayer((Player)e.getEntity(), CitizensAPI.getNPCRegistry().getNPC(e.getDamager()).getName());
				}
			}
			return;
		}
		
		if(e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.hasMetadata("damages")) {
				e.setDamage(arrow.getMetadata("damages").get(0).asDouble());
			}
			if(main.getDonjonManager().getBossManager().isBoss(e.getEntity())) {
				if(arrow.getShooter() != null && arrow.getShooter() instanceof Player)
					main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), ((Player)arrow.getShooter()).getUniqueId(), e.getDamage());
				else
					main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), null, e.getDamage());
			}
		}
		
		// Si l'entité qui attaque c'est un Player
		if( ! (e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();

		int playerKarma = main.getClasseManager().getPlayerData(p).getCurrentKarma();
		
		if(p.getGameMode() != GameMode.CREATIVE) {
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
			
			if(e.getEntity() instanceof Player) {
				PlayerData targetData = main.getClasseManager().getPlayerData((Player)e.getEntity());
				int targetKarma = targetData.getCurrentKarma();
				int karma = -2;
				if(((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
					karma -= 100;
				}
				if(targetKarma <= 300) {
					karma *= -1;
					targetData.deltaKarma(karma/2);
					if(playerKarma < 300)
						karma /= 2;
				}
				main.getClasseManager().getPlayerData(p).deltaKarma(karma);
			}
			
		}
		
		
		
		if(e.getEntity() instanceof Player && ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
			if(((LivingEntity)e.getEntity()).getHealth() <= ((EntityDamageEvent)e).getFinalDamage()) {
				alertDeathPlayer((Player)e.getEntity(), p.getName());
			}
		}
		
		if(effects.hasEffect(Damocles.EFFECT_NAME, p)) {
			e.setDamage(e.getDamage() * 2);
			p.damage(Damocles.DAMAGES);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, .6f, .8f);
			if(e.getEntity() instanceof Player)
				((Player)e.getEntity()).playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 2f, .8f);
		}
		
		if(effects.hasEffect(AcierBrut.EFFECT_NAME, p)) {
			p.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_SPIDER, 1f, .5f);
			for(PotionEffect effect : AcierBrut.effects)
				((LivingEntity)e.getEntity()).addPotionEffect(effect);
		}
		
		if(effects.hasEffect(AcierPrecis.EFFECT_NAME, p)) {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, .5f);
			((LivingEntity)e.getEntity()).addPotionEffect(AcierPrecis.effect);
		}
		
		if(main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), e.getDamager().getUniqueId(), e.getDamage())) {
			e.setCancelled(true);
		}
		
		
		
		if(main.getSuperMobManager().damageMob(e.getEntity(), p.getUniqueId(), e.getDamage())) {
			e.setCancelled(true);
			return;
		}
		
	}
	
	private final static String SKULL = new String(Character.toChars(10060));
	
	public void alertDeathPlayer(Player killed, String killer) {
		String message = ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + killed.getName() + ChatColor.GRAY;
		if(killer == null) {
			playerDeath(killed, message + " s'est suicidé.");
		} else {
			playerDeath(killed, message + " a été tué par " + killer + ChatColor.GRAY + ".");
		}
	}
	
	public void playerDeath(Player source, String msg) {
		main.getConsole().sendMessage(msg);
		if(main.getDonjonManager().getContainerDonjon(source) == null) {
			Bukkit.getOnlinePlayers().forEach(pl -> pl.sendMessage(msg));
			return;
		}
		main.getDonjonManager().getContainerDonjon(source).getJoinedPlayers().forEach(id -> {
			Player pl = Bukkit.getPlayer(id);
			if(pl != null && pl.isOnline())
				pl.sendMessage(msg);
		});
	}
	
	@EventHandler
	public void entityDamaged(EntityDamageEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		
		if(e.getEntity() instanceof Player && ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
			Player p = (Player) e.getEntity();
			if(main.getChunkManager().isSafe(p.getLocation())) {
				e.setCancelled(true);
				return;
			}
			if(e.getFinalDamage() < p.getHealth())
				return;
			if(e.getCause() == DamageCause.CRAMMING || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.HOT_FLOOR || e.getCause() == DamageCause.MELTING) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " est mort dans d'horribles flammes.");
			} else if(e.getCause() == DamageCause.LAVA) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " est mort dans dans lave.");
			} else if(e.getCause() == DamageCause.FALL) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " est tombé de haut.");
			}else if(e.getCause() == DamageCause.THORNS) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " ne pensait pas que ça piquait.");
			}else if(e.getCause() == DamageCause.SUICIDE || e.getCause() == DamageCause.VOID) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " a mit fin à ses jours.");
			}else if(e.getCause() == DamageCause.DROWNING) {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " s'est noyé...");
			}
		}
		
		if(main.getDonjonManager().getBossManager().isBoss(e.getEntity())) {
			LivingEntity en = (LivingEntity) e.getEntity();
			e.setCancelled(true);
			if ( e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.ENTITY_SWEEP_ATTACK || e.getCause() == DamageCause.PROJECTILE ) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				playerDamageEntity(event);
				if(e.getCause() == DamageCause.PROJECTILE) { // on bute les arrow sinon c'est moche
					if(event.getDamager() instanceof Arrow)
						event.getDamager().remove();
				}
			} else {
				main.getDonjonManager().getBossManager().damageBoss(en, null, e.getFinalDamage());
			}
			//Bukkit.broadcastMessage("§e->"+e.getCause() + "§a - " + (e instanceof EntityDamageByEntityEvent ? "oui" : "§cnon") + "§e - " + e.getFinalDamage());
			e.setCancelled(true);
			return;
		}
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