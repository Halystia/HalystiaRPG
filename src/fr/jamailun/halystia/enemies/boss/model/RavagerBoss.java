package fr.jamailun.halystia.enemies.boss.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.boss.Boss;
import fr.jamailun.halystia.spells.spellEntity.CircleEffect;
import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;
import fr.jamailun.halystia.spells.spellEntity.EffectSpellEntity;
import fr.jamailun.halystia.utils.RandomString;

public class RavagerBoss extends Boss {

	private final static int HEALTH = 2500;
	
	private Ravager mob;
	
	public RavagerBoss() {
		maxHealth = health = HEALTH;
		MAX_INVOCATIONS = 8;
		bar = Bukkit.createBossBar(getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
		bar.setVisible(true);
	}
	private int noPlayers = 0;
	private int counter = 0;
	private final static int ACTION_EVERY_SECONDS = 5;
	@Override
	protected void doAction() {
		checkBarPlayers(mob.getLocation());
		counter++;
		if(counter < ACTION_EVERY_SECONDS)
			return;
		counter = 0 - new Random().nextInt(2);
		
		LivingEntity closest = getClosestEntity(mob.getLocation(), 40, true);
		if ( closest == null ) {
			
		}
		
		if(closest == null) {
			noPlayers++;
			if(noPlayers >= 30) {
				noPlayers = 20;
				health += 100;
				if(maxHealth < health)
					health = maxHealth;
			}
			return;
		} else {
			if(closest instanceof Player) {
				if(((Player)closest).getGameMode() != GameMode.CREATIVE && ((Player)closest).getGameMode() != GameMode.CREATIVE)
					mob.setTarget(closest);
			} else {
				mob.setTarget(closest);
			}
		}
		noPlayers = 0;
		
		
		
		int rand = RandomString.randInt(0, 100);
		if(rand < 20 && canInvoke(getMainUUID(), 1)) {
			invoke();
		} else if(rand < 40) {
			catchEntities();
			counter -= 3;
			new BukkitRunnable() {
				@Override
				public void run() {
					aoe();
				}
			}.runTaskLater(HalystiaRPG.getInstance(), 2*20L);
		} else if(rand < 80 && closest != null) {
			goToEntity(closest);
		} else {
			flame();
		}
	}
	private final static List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.SLOW, 60, 0, false), new PotionEffect(PotionEffectType.WEAKNESS, 120, 1, false));
	private void flame() {

		makeSound(mob.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, .6f);
			
		new BukkitRunnable() {
			@Override
			public void run() {
				Optional<LivingEntity> close = getCloseEntities(mob.getLocation(), 30).stream().findAny();
				if( ! close.isPresent() )
					return;
				EffectAndDamageSpellEntity spell = new EffectAndDamageSpellEntity(mob.getEyeLocation(), mob, 10, 3, false, false);
				spell.setPotionEffects(effects);
				spell.setFireTick(20*4);
				spell.setDamages(5);
				spell.setDirection(close.get().getEyeLocation().toVector().subtract(mob.getEyeLocation().toVector()).normalize().multiply(2.1));
				spell.addSoundEffect(Sound.BLOCK_LAVA_AMBIENT, 2f, .2f);
				spell.addParticleEffect(Particle.LAVA, 40, 1, .66, .01);
				spell.addParticleEffect(Particle.FLAME, 50, 1, 1, .05);
				spell.addParticleEffect(Particle.SMOKE_NORMAL, 30, 1, 1, .1);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 20L);
		
	}

	private void aoe() {
		for(LivingEntity en : super.getCloseEntities(mob.getLocation(), 6)) {
			en.damage(5); //TODO particules.
		}
	}
	
	private void invoke() {
		Phantom inv = mob.getWorld().spawn(mob.getLocation().clone().add(0, 3, 0), Phantom.class);
		new EffectSpellEntity(mob.getEyeLocation().clone().add(0,1,0), mob, 1, new ArrayList<>(), 1, false).addParticleEffect(Particle.ENCHANTMENT_TABLE, 300, .2, .1, .4);
		inv.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
		HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add(inv, mob, false, this, 5);
		invocations.add(inv);
		inv.setCustomName(ChatColor.DARK_AQUA+"Illusion");
		inv.setCustomNameVisible(true);
	}
	
	private void catchEntities() {
		
		new CircleEffect(6, 3, Particle.FLAME).effect(mob.getLocation(), 2, 2);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for(LivingEntity en : getCloseEntities(mob.getLocation(), 6)) {
					Vector dir = mob.getLocation().toVector().subtract(en.getEyeLocation().toVector()).normalize().multiply(.5);
					dir.add(new Vector(0, .2, 0));
					en.setVelocity(en.getVelocity().add(dir));
				}
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 40L);
		
		
	}
	
	private void goToEntity(LivingEntity target) {
		
	}
	
	@Override
	public void oneIsDead(UUID uuid) {
		for(LivingEntity en : invocations) {
			if(en.getUniqueId().equals(uuid)) {
				en.getWorld().spawnParticle(Particle.FLASH, en.getLocation().getX(), en.getLocation().getY(), en.getLocation().getZ(), 50, .1, .1, .1, .05);
				super.damage(50);
				break;
			}
		}
		
		invocations.removeIf(en -> en.getUniqueId().equals(uuid));
	}

	@Override
	public boolean canMove() {
		return true;
	}

	@Override
	protected void damageAnimation() {
		if(mob != null) {
			mob.playEffect(EntityEffect.HURT);
		}
	}

	@Override
	protected void killed() {
		bar.setVisible(false);
		exists = false;
		invocations.forEach(en -> en.remove());
		invocations.clear();
		final Location loc = mob.getLocation().clone();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(mob != null)
					mob.remove();
				mob = null;
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 10*10L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				loc.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(loc) <= 30).forEach(p -> {
					safeExit(donjon, p);
				});
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 12*10L);
		
		for(final Player pl : donjon.getBossLocation().getWorld().getPlayers()) {
			if(pl.getLocation().distance(donjon.getBossLocation()) < 40) {
				for(int i = 0; i < 10; i++) {
					final int h = i;
					new BukkitRunnable() {
						@Override
						public void run() {
							mob.teleport(loc.clone().add(0, 0.1*(double)h, 0));
							pl.spawnParticle(Particle.EXPLOSION_LARGE,
									loc.getX(), loc.getY(), loc.getZ(), 
									2,
									5, 3, 5,
									.5
							);
							pl.spawnParticle(Particle.FLASH,
									loc.getX(), loc.getY(), loc.getZ(), 
									4,
									5, 3, 5,
									1
							);
							pl.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1.2f);
						}
					}.runTaskLater(HalystiaRPG.getInstance(), i*10L);
					
				}
				
				pl.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 10f, .1f);
			}
		}
	}

	@Override
	public double distance(Location loc) {
		if(mob == null)
			return Double.MAX_VALUE;
		if( ! mob.getLocation().getWorld().equals(loc.getWorld()))
			return Double.MAX_VALUE;
		return loc.distance(mob.getLocation());
	}

	@Override
	public String getCustomName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Maître des ruines";
	}

	@Override
	protected boolean isBoss(UUID uuid) {
		if(mob == null)
			return false;
		return uuid.equals(mob.getUniqueId());
	}

	@Override
	public List<ItemStack> getLoots() {
		List<ItemStack> loots = new ArrayList<>();
		
		return loots;
	}

	@Override
	public int getXp() {
		return 300;
	}

	@Override
	public void purge() {
		if(mob != null)
			mob.remove();
		damagers.clear();
		exists = false;
		mob = null;
		stopLoop();
		bar.setVisible(false);
		invocations.forEach(en -> en.remove());
		invocations.clear();
	}

	@Override
	protected boolean spawn(DonjonI donjon) {
		this.donjon = donjon;
		Location loc = donjon.getBossLocation();
		
		if(mob != null)
			return false;
		
		mob = loc.getWorld().spawn(loc, Ravager.class);
		mob.setCustomNameVisible(true);
		health = HEALTH;
		
		damagers.clear();
		bar.setVisible(true);

		invocations.forEach(en -> en.remove());
		invocations.clear();
		updateBar();
		
		return true;
	}

	@Override
	public UUID getMainUUID() {
		return mob.getUniqueId();
	}

}