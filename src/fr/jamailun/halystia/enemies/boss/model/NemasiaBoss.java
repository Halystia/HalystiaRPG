package fr.jamailun.halystia.enemies.boss.model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.boss.Boss;

public class NemasiaBoss extends Boss {

	private Location loc;
	
	private Giant giant;
	private Ghast head;
	
	public NemasiaBoss() {
		maxHealth = health = 2000;
		bar = Bukkit.createBossBar(getCustomName(), BarColor.RED, BarStyle.SEGMENTED_6, BarFlag.DARKEN_SKY);
		
	}
	
	private int counter = 0;
	private final static int ACTION_EVERY_SECONDS = 4;
	@Override
	protected void doAction() {
		counter++;
		if(counter < ACTION_EVERY_SECONDS)
			return;
		counter = 0;
		
		double random = Math.random()*100; //entre 0 et 100
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
		Player target = getClosestPlayer(50);
		if(target == null)
			return;
		
		
	}
	
	private void summonCubes() {
		List<Player> targets = getClosePlayers(80);
		if(targets.isEmpty())
			return;
		
		
	}
	
	private void lightStrike() {
		Player target = getClosestPlayer(50);
		if(target == null)
			return;
		
		
	}
	
	private void fire() {
		Player target = getClosestPlayer(50);
		if(target == null)
			return;
		
		
	}
	
	private Player getClosestPlayer(double maxDistance) {
		Player player = null;
		double distance = maxDistance+0.1;
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) < distance) {
				player = pl;
				distance = pl.getLocation().distance(loc);
			}
		}
		return player;
	}
	
	private List<Player> getClosePlayers(double maxDistance) {
		return loc.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(loc) <= maxDistance).collect(Collectors.toList());
	}
	
	
	
	@Override
	public boolean canMove() {
		return false;
	}
	
	@Override
	protected void killed() {
		if(loc == null)
			throw new IllegalStateException("Location is null !");
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
		head.remove();
		giant.remove();
		damagers.clear();
	}

	@Override
	public void spawn(DonjonI donjon) { // mettre donjon et choper une loc de spawn de boss pour le dj.
		this.loc = donjon.getBossLocation();
		giant = loc.getWorld().spawn(loc, Giant.class);
		
		head = loc.getWorld().spawn(loc, Ghast.class);
		giant.addPassenger(head);
		head.setCustomName(getCustomName());
		head.setCustomNameVisible(true);
		head.setGlowing(true);
	}
}