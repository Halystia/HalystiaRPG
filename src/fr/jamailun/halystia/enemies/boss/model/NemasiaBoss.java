package fr.jamailun.halystia.enemies.boss.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.boss.Boss;
import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;
import fr.jamailun.halystia.spells.spellEntity.SpellEntity;

public class NemasiaBoss extends Boss {

	public static final int MAX_INVOCATIONS = 5;
	public static final int HEALTH = 5000;
	public static final int HEALTH_PER_CUBE = 200;
	public static final int CUBES_HEALTH = 1;
	public static final int CUBES_DAMAGES = 5;
	
	private Location loc;
	
	private Giant giant;
	private Ghast head;
	
	private final Random rand = new Random();
	
	public NemasiaBoss() {
		maxHealth = health = HEALTH;
		bar = Bukkit.createBossBar(getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
		bar.setVisible(true);
	}
	
	private int counter = 0;
	private final static int ACTION_EVERY_SECONDS = 5;
	@Override
	protected void doAction() {
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) < 25) {
				if( ! bar.getPlayers().contains(pl)) {
					bar.addPlayer(pl);
				}
			} else {
				if(bar.getPlayers().contains(pl)) {
					bar.removePlayer(pl);
				}
			}
		}
		counter++;
		if(counter < ACTION_EVERY_SECONDS)
			return;
		counter = 0 - rand.nextInt(5);
		
		double random = rand.nextInt(100)+1;
		if ( random <= 20 )
			shotFireBall();			// 20%
		else if ( random <= 50 )
			summonCubes();			// 30%
		else if ( random <= 70 )
			lightStrike();			// 20%
		else
			fire();					// 30%
	}
	
	private void shotFireBall() {
		final Player target = getClosestPlayer(loc, 25, true);
		if(target == null)
			return;
		
		for(int i = 1; i <= 3; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Vector direction = target.getLocation().toVector().subtract(head.getLocation().toVector()).normalize().multiply(2);
					direction = direction.multiply(2);
					Fireball ball = head.launchProjectile(Fireball.class);
					ball.setShooter(giant);
					ball.setYield(5);
					ball.setDirection(direction);
					makeSound(head.getLocation(), Sound.ENTITY_GHAST_SCREAM, .7f);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), 20L*i);
		}
	}
	
	private void summonCubes() {
		if( ! canInvoke(giant.getUniqueId(), 1))
			return;
		List<Player> targets = getClosePlayers(loc, 25);
		if(targets.isEmpty())
			return;
		for(Player pl : targets) {
			MagmaCube cube = loc.getWorld().spawn(pl.getLocation().add(0, 10, 0), MagmaCube.class);
			cube.setSize(2);
			cube.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(CUBES_HEALTH);
			cube.setHealth(CUBES_HEALTH);
			HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add(cube, giant, false, this, CUBES_DAMAGES);
			invocations.add(cube.getUniqueId());
		}
		
	}
	
	private void lightStrike() {
		Player target = getClosestPlayer(loc, 10, false);
		if(target == null)
			return;
		new BukkitRunnable() {
			@Override
			public void run() {
				loc.getWorld().strikeLightningEffect(target.getLocation());
				target.damage(10);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 30L);
		makeSound(head.getLocation(), Sound.ENTITY_GHAST_WARN, .7f);
	}
	
	private final static List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 0, false), new PotionEffect(PotionEffectType.SLOW, 120, 1, false));
	private void fire() {
		Player target = getClosestPlayer(loc, 25, true);
		if(target == null)
			return;
		
		SpellEntity spell = new EffectAndDamageSpellEntity(head.getLocation(), giant, 40, effects, 2, false, 100, 3.0, 0, false);
		spell.setDirection(target.getLocation().toVector().subtract(head.getLocation().toVector()).normalize().multiply(.7));
		spell.addSoundEffect(Sound.BLOCK_CAMPFIRE_CRACKLE, 2f, .1f);
		spell.addParticleEffect(Particle.END_ROD, 40, 1, 1, .01);
		spell.addParticleEffect(Particle.FLAME, 100, 1.5, 1.5, .05);
		spell.ignore(head.getUniqueId());
		makeSound(head.getLocation(), Sound.ENTITY_GHAST_SHOOT, .7f);
	}
	
	
	
	@Override
	public boolean canMove() {
		return false;
	}
	
	@Override
	protected void killed() {
		if(loc == null)
			throw new IllegalStateException("Location is null !");

		exists = false;
		head = null;
		giant = null;
		stopLoop();
		
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) < 200) {
				pl.spawnParticle(Particle.EXPLOSION_LARGE,
						loc.getX(), loc.getY(), loc.getZ(), 
						10,
						.5, .5, .5,
						.5
				);
				pl.spawnParticle(Particle.FLASH,
						loc.getX(), loc.getY(), loc.getZ(), 
						50,
						5, 5, 5,
						.1
				);
				pl.spawnParticle(Particle.DRIP_LAVA,
						loc.getX(), loc.getY(), loc.getZ(), 
						300,
						.5, .5, .5,
						1
				);
				pl.playSound(loc, Sound.ENTITY_WITHER_DEATH, 4f, .8f);
			}
		}
	}

	@Override
	public double distance(Location loc) {
		if(loc == null)
			throw new IllegalStateException("Location is null !");
		return this.loc.distance(loc);
	}

	@Override
	public String getCustomName() {
		return ChatColor.DARK_RED +""+ ChatColor.BOLD + "Général démonique";
	}

	@Override
	protected boolean isBoss(UUID id) {
		if(!exists)
			return false;
		return id.equals(head.getUniqueId()) || id.equals(giant.getUniqueId());
	}

	@Override
	public List<ItemStack> getLoots() {
		return Arrays.asList(new ItemStack(Material.EMERALD, 32));
	}

	@Override
	public int getXp() {
		return 10000;
	}

	@Override
	public void purge() {
		if(head != null)
			head.remove();
		if(giant != null)
			giant.remove();
		damagers.clear();
		exists = false;
		head = null;
		giant = null;
		stopLoop();
		bar.setVisible(false);
	}

	@Override
	public boolean spawn(DonjonI donjon) {
		this.loc = donjon.getBossLocation();
		
		if(giant != null || head != null)
			return false;
		
		giant = loc.getWorld().spawn(loc, Giant.class);
		giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
		giant.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		giant.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		giant.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
		giant.getEquipment().setItemInMainHand(new ItemStack(Material.BLAZE_ROD));
		head = loc.getWorld().spawn(loc, Ghast.class);
		giant.addPassenger(head);
		head.setCustomName(getCustomName());
		head.setCustomNameVisible(true);
		head.setGlowing(true);
		
		health = HEALTH;
		
		damagers.clear();
		bar.setVisible(true);
		
		updateBar();
		
		return true;
	}

	@Override
	public boolean canInvoke(UUID uuid, int howMany) {
		if(invocations.size() + howMany >= MAX_INVOCATIONS)
			return false;
		return true;
	}

	@Override
	public void oneIsDead(UUID uuid) {
		invocations.remove(uuid);
		super.damage(HEALTH_PER_CUBE);
		makeSound(loc, Sound.ENTITY_GHAST_HURT, .8f);
	}

	@Override
	public UUID getMainUUID() {
		if(!exists)
			return UUID.randomUUID();
		return giant.getUniqueId();
	}

	@Override
	protected void damageAnimation() {
		if(giant != null && head != null) {
			giant.playEffect(EntityEffect.HURT);
			head.playEffect(EntityEffect.HURT);
		}
	}
}