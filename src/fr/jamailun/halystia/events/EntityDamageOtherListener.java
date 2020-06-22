package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mcmonkey.sentinel.SentinelTrait;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.DamageReason;
import fr.jamailun.halystia.custom.PlayerEffectsManager;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.players.SkillSet;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierBrut;
import fr.jamailun.halystia.spells.newSpells.epeiste.AcierPrecis;
import fr.jamailun.halystia.spells.newSpells.epeiste.Damocles;
import fr.jamailun.halystia.spells.spellEntity.InvocationsManager;
import fr.jamailun.halystia.utils.PlayerUtils;
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
		
		//System.out.println("dmgs !");
		
		double damages = e.getDamage();

		final PlayerData pdamager = (e.getDamager() instanceof Player) ? main.getClasseManager().getPlayerData((Player)e.getDamager()) : null;
		final PlayerData pcible = (e.getEntity() instanceof Player) ? main.getClasseManager().getPlayerData((Player)e.getEntity()) : null;

		if(e.getDamager() instanceof Arrow) {

			
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.hasMetadata("damages")) {
				damages = arrow.getMetadata("damages").get(0).asDouble();
			}
			System.out.println("damager = arrow, §cdamages="+damages);
			if(main.getDonjonManager().getBossManager().isBoss(e.getEntity())) {
				if(arrow.getShooter() != null && arrow.getShooter() instanceof Player)
					main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), ((Player)arrow.getShooter()).getUniqueId(), damages);
				else
					main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), null, damages);
			}
		}
		
		// DAMAGER == INVOCATION
		if(invocs.contains(e.getDamager().getUniqueId())) {
			Entity damager = e.getDamager();

			System.out.println("damager = invoc");
			
			//MàJ des dommages
			damages = invocs.getDamages(damager);

			if(main.getDonjonManager().getBossManager().damageBoss(e.getEntity(), invocs.getMasterOf(damager.getUniqueId()), damages)) {
				e.setCancelled(true);
				return;
			}

			//On annule si jamais c'est le créateur !
			if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();

				if(invocs.isMasterOf(p, damager)) {
					if(damager instanceof Arrow)
						damager.remove();
					damages = 0;
					e.setDamage(0);
					e.setCancelled(true);
					return;
				}

			}

			if(main.getMobManager().hasMob(e.getEntity().getEntityId())) {
				LivingEntity liv = (LivingEntity) e.getEntity();
				if(liv.getHealth() - e.getDamage() <= 0) {
					//L'invocation tue une saloperie ! Et elle tue un enemymob !
					EnemyMob mobType = main.getMobManager().getWithEntityId(liv.getEntityId());
					if(mobType == null)
						return;
					Entity masterEntity = Bukkit.getEntity(invocs.getMasterOf(liv.getUniqueId()));
					if(masterEntity == null || masterEntity.getType() != EntityType.PLAYER)
						return;
					main.getClasseManager().getPlayerData((Player)masterEntity).addXp(mobType.getXp() / 2);
					return;
				}
			}
		}
		
		//DAMAGER == PLAYER
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			LivingEntity target = (LivingEntity) e.getEntity();

			damages = playerAttacked(p, target, pcible);
			
			p.sendMessage("Attaque de §c"+damages);
			
			if(damages == -1) {
				e.setCancelled(true);
				return;
			}
		}
		
		//DAMAGER == MOB
		if(main.getMobManager().hasMob(e.getDamager().getEntityId())) {			

			System.out.println("damager = mob");
			
			EnemyMob mob = main.getMobManager().getWithEntityId(e.getDamager().getEntityId());
			damages = mob.getCustomDamages();
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity target = (LivingEntity) e.getEntity();
				if(mob.isPoisonous())
					target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 1));
				if(mob.isWitherous())
					target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*5, 1));
			}
		}

		// DAMAGER == BOSS
		if(main.getDonjonManager().getBossManager().isBoss(e.getDamager())) {
			// TODO si on rajoute les degats des boss : damages = main.getDonjonManager().getBossManager().getBoss(e.getDamager()).getDamages()
			if(pcible != null) {
				e.setCancelled(true);
		//		pcible.damage(damages, e.getDamager().getUniqueId(), DamageReason.BOSS);
			}
		}
		
		// DAMAGER == SUPERMOB
		if(main.getSuperMobManager().isOne(e.getDamager())) {
			//idem...
			e.setCancelled(true);
		}
		
		e.setDamage(damages);

		// TARGET == PLAYER
		if(pcible != null) {
			//Esquive ?
			if ( Math.random() < 0.01 * pcible.getSkillSetInstance().getLevel(SkillSet.SKILL_AGILITE) ) {
				damages = 0;
				e.setDamage(0);
				e.setCancelled(true);
				new PlayerUtils(pcible.getPlayer()).sendActionBar(ChatColor.GOLD + ""+ChatColor.BOLD+"Esquive !");
				e.getEntity().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getEntity().getLocation(), 50); 
				return;
			}
			DamageReason reason = DamageReason.NONE;
			UUID uuidDamager = e.getDamager().getUniqueId();
			if(pdamager != null) {
				reason = DamageReason.PLAYER;
			} else if(CitizensAPI.getNPCRegistry().isNPC(e.getDamager()) && CitizensAPI.getNPCRegistry().getNPC(e.getDamager()).hasTrait(SentinelTrait.class)) {
				reason = DamageReason.SENTINEL;
			} else if(main.getSuperMobManager().isOne(e.getDamager())) {
				reason = DamageReason.SUPERMOB;
			} else if(main.getDonjonManager().getBossManager().isBoss(e.getDamager())) {
				reason = DamageReason.BOSS;
			} else if(main.getMobManager().isMobRecognized(e.getDamager())) {
				reason = DamageReason.MOB;
			} else if(main.getSpellManager().getInvocationsManager().contains(e.getDamager().getUniqueId())) {
				reason = DamageReason.INVOCATION;
				uuidDamager = main.getSpellManager().getInvocationsManager().getMasterOf(e.getDamager().getUniqueId());
			} else if(((EntityDamageEvent)e).getCause() == DamageCause.FIRE || ((EntityDamageEvent)e).getCause() == DamageCause.FIRE_TICK || ((EntityDamageEvent)e).getCause() == DamageCause.LAVA) {
				reason = DamageReason.FIRE;
				uuidDamager = e.getEntity().getUniqueId();
			}
			pcible.damage(damages, uuidDamager, reason);
			e.setDamage(0);
			e.setCancelled(true);
			return;
		}

		// TARGET == SUPERMOB
		if(main.getSuperMobManager().damageMob(e.getEntity(), e.getDamager().getUniqueId(), damages)) {
			e.setCancelled(true);
			return;
		}
		
	}

	private final static String SKULL = new String(Character.toChars(10060));

	public static void alertDeathPlayer(Player killed, String killer) {
		String message = ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + killed.getName() + ChatColor.GRAY;
		if(killer == null) {
			playerDeath(killed, message + " s'est suicidé.");
		} else {
			playerDeath(killed, message + " a été tué par " + killer + ChatColor.GRAY + ".");
		}
	}

	public static void playerDeath(Player source, String msg) {
		HalystiaRPG main = HalystiaRPG.getInstance();
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
	public void playerHealth(EntityRegainHealthEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if( ! (e.getEntity() instanceof Player))
			return;
		if(e.getRegainReason() == RegainReason.WITHER_SPAWN || e.getRegainReason() == RegainReason.ENDER_CRYSTAL || e.getRegainReason() == RegainReason.MAGIC)
			return;
		main.getClasseManager().getPlayerData((Player)e.getEntity()).heal(e.getAmount() * 30);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void entityDamaged(EntityDamageEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;

		if(e.getEntity() instanceof Player && ! CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
			Player p = (Player) e.getEntity();
			PlayerData pc = main.getClasseManager().getPlayerData(p);
			if(main.getChunkManager().isSafe(p.getLocation())) {
				e.setCancelled(true);
				return;
			}
			boolean dead = false;
			switch(e.getCause()) {
			case CONTACT:
				dead = pc.damage(30, null, DamageReason.NONE, true);
				break;
			case CRAMMING:
				dead = pc.damage(2, null, DamageReason.NONE, true);
				break;
			case DRAGON_BREATH:
				dead = pc.damage(300, null, DamageReason.NONE, true);
				break;
			case DROWNING:
				dead = pc.damage(100, null, DamageReason.NONE, true);
				break;
			case DRYOUT:
			case MELTING:
				//pas players
				break;
			case ENTITY_ATTACK:
			case ENTITY_SWEEP_ATTACK:
			case PROJECTILE:
				if(e.getEntity() instanceof Player)
					return;
				break;
			case CUSTOM:
				dead = pc.damage(e.getDamage(), null, DamageReason.NONE);
				break;
			case FIRE:
			case HOT_FLOOR:
				dead = pc.damage(20, null, DamageReason.FIRE, true);
				break;
			case FIRE_TICK:
				dead = pc.damage(10, null, DamageReason.FIRE, true);
				break;
			case LAVA:
				dead = pc.damage(40, null, DamageReason.FIRE, true);
				break;
			case ENTITY_EXPLOSION:
			case LIGHTNING:
			case BLOCK_EXPLOSION:
				dead = pc.damage(e.getDamage() * 4, null, DamageReason.FIRE, true);
				break;
			case MAGIC:
				dead = pc.damage(e.getDamage() * 30, null, DamageReason.NONE);
				break;
			case POISON:
				PotionEffect effectP = p.getPotionEffect(PotionEffectType.POISON);
				int levelP = (effectP != null ? effectP.getAmplifier() : 0) + 1;
				dead = pc.damage(20 * levelP, null, DamageReason.POISON, true);
				break;
			case STARVATION:
				dead = pc.damage(30, null, DamageReason.POISON, true);
				break;
			case SUFFOCATION:
				dead = pc.damage(30, null, DamageReason.NONE, true);
				break;
			case FLY_INTO_WALL:
				dead = pc.damage(25, null, DamageReason.NONE, true);
				break;
			case SUICIDE:
				dead = pc.damage(pc.getHealth() * 100, null, DamageReason.NONE, true);
				break;
			case THORNS:
			case FALL:
			case FALLING_BLOCK:
				dead = pc.damage(e.getDamage() * 20, null, DamageReason.NONE, true);
				break;
			case VOID:
				dead = pc.damage(500, null, DamageReason.NONE, true);
				break;
			case WITHER:
				PotionEffect effectW = p.getPotionEffect(PotionEffectType.POISON);
				int levelW = (effectW != null ? effectW.getAmplifier() : 0) + 1; 
				dead = pc.damage(30 * levelW, null, DamageReason.POISON, true);
				break;
			}

			e.setCancelled(true);
			e.setDamage(0);
			
			if( ! dead)
				return;
			if(e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.HOT_FLOOR) {
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
			} else {
				playerDeath(p ,ChatColor.DARK_RED + SKULL + " " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " est malencontreusement décédé...");
			}
			return;
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
			if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA || e.getCause() == DamageCause.LIGHTNING || e.getCause() == DamageCause.CRAMMING) {
				e.getEntity().setFireTicks(0);
				e.setCancelled(true);
			}
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

	private double playerAttacked(Player p, LivingEntity target, PlayerData cibleData) {
		double localModifier = 1;

		PlayerData pc = main.getClasseManager().getPlayerData(p);

		if(p.getGameMode() != GameMode.CREATIVE) {
			Classe classe =pc.getClasse();
			if(p.getInventory().getItemInMainHand() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInMainHand());
				if(classe != ob && ob != Classe.NONE) {
					//e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
					return -1;
				}
			}

			if(p.getInventory().getItemInOffHand() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInOffHand());
				if(classe != ob && ob != Classe.NONE) {
					//e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
					return -1;
				}
			}
		}

		int karma = pc.getCurrentKarma();
		if(karma <= -300)
			if(pc != null)
				localModifier = 1.1;
		if(karma >= 300)
			if(target instanceof Monster)
				localModifier = 1.1;
		// Add the % of critical hit
		if ( Math.random() < 0.01 * pc.getSkillSetInstance().getLevel(SkillSet.SKILL_FORCE) ) {
			localModifier *= 1.5;
			if(pc != null)
				pc.getPlayer().sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Votre agresseur vous a assené un coup critique. +50% de dégâts.");
			new PlayerUtils(pc.getPlayer()).sendActionBar(ChatColor.GOLD + "Coup critique !"+ChatColor.BOLD+" +50% de dégâts.");
		}

		if(effects.hasEffect(Damocles.EFFECT_NAME, p)) {
			localModifier *= 2;
			p.damage(Damocles.DAMAGES);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, .6f, .8f);
			if(pc != null)
				pc.getPlayer().playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 2f, .8f);
		}

		if(effects.hasEffect(AcierBrut.EFFECT_NAME, p)) {
			p.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_SPIDER, 1f, .5f);
			for(PotionEffect effect : AcierBrut.effects)
				target.addPotionEffect(effect);
		}

		if(effects.hasEffect(AcierPrecis.EFFECT_NAME, p)) {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, .5f);
			target.addPotionEffect(AcierPrecis.effect);
		}

		if(cibleData != null) {
			int targetKarma = cibleData.getCurrentKarma();
			int karma2 = -2;
			if(cibleData.getHealth() <= pc.getDamages() * localModifier) {
				karma2 -= 100;
			}
			if(targetKarma <= -300) {
				karma2 *= -1;
				cibleData.deltaKarma(karma2/2);
				if(pc.getCurrentKarma() < -300)
					karma2 /= 2;
			}
			pc.deltaKarma(karma2);
		}

		return pc.getDamages() * localModifier;
	}
}