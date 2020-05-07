package fr.jamailun.halystia.enemies.boss.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.boss.Boss;
import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;
import fr.jamailun.halystia.spells.spellEntity.EffectSpellEntity;
import fr.jamailun.halystia.spells.spellEntity.SpellEntity;
import fr.jamailun.halystia.utils.ItemBuilder;

public class BossRuines extends Boss {

	private final static int HEALTH = 1500;
	
	private Zombie mob;
	
	public BossRuines() {
		maxHealth = health = HEALTH;
		MAX_INVOCATIONS = 3;
		bar = Bukkit.createBossBar(getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
		bar.setVisible(true);
	}
	private int noPlayers = 0;
	private int counter = 0;
	private final static int ACTION_EVERY_SECONDS = 7;
	@Override
	protected void doAction() {
		for(Player pl : mob.getWorld().getPlayers()) {
			if(pl.getLocation().distance(mob.getLocation()) < 30) {
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
		counter = 0 - new Random().nextInt(2);
		
		Player closest = getClosestPlayer(mob.getEyeLocation(), 30, false);
		if(closest == null) {
			noPlayers++;
			if(noPlayers >= 30) {
				noPlayers = 20;
				health += 100;
				if(maxHealth < health)
					health = maxHealth;
			}
			return;
		}
		noPlayers = 0;
		
		if(closest.getGameMode() != GameMode.CREATIVE && closest.getGameMode() != GameMode.SPECTATOR)
			mob.setTarget(closest);
		if(Math.random() < .6 && canInvoke(getMainUUID(), 1)) {
			invoke();
		} else {
			flame(closest);
		}
	}
	private final static List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.SLOW, 60, 0, false), new PotionEffect(PotionEffectType.WEAKNESS, 120, 1, false));
	private void flame(LivingEntity target) {
		if(target == null)
			return;
		makeSound(mob.getLocation(), Sound.ENTITY_WITHER_SHOOT, .7f);
		new BukkitRunnable() {
			@Override
			public void run() {
				SpellEntity spell = new EffectAndDamageSpellEntity(mob.getEyeLocation(), mob, 8, effects, 2, false, 100, 4.0, 0.1, false);
				spell.setDirection(target.getEyeLocation().toVector().subtract(mob.getEyeLocation().toVector()).normalize().multiply(1.5));
				spell.addSoundEffect(Sound.BLOCK_CAMPFIRE_CRACKLE, 2f, .1f);
				spell.addParticleEffect(Particle.END_ROD, 40, 1, .66, .01);
				spell.addParticleEffect(Particle.FLAME, 100, 1, 1, .05);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 20L);
		
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
		
		mob = loc.getWorld().spawn(loc, Zombie.class);
		mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.01);
		mob.getEquipment().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).shine().toItemStack());
		mob.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		mob.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
		mob.getEquipment().setItemInMainHand(new ItemBuilder(Material.DIAMOND_AXE).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 10, Operation.ADD_NUMBER, EquipmentSlot.HAND).toItemStack());
		mob.setBaby(false);
		mob.setCustomName(getCustomName());
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