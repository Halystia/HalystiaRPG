package fr.jamailun.halystia.enemies.supermobs.models;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.supermobs.SuperMob;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.Laser;

public class OeilAntique extends SuperMob {

	private Laser laser;
	private EnderCrystal entity;

	private final static long PERIOD = 20L;
	private final double range, damages;
	private final long cooldownShot;
	
	public OeilAntique(Location loc, double health, double range, double damages, long cooldownShots, long cooldownRespawn) {
		super(loc, health, cooldownRespawn);
		
		this.range = range;
		this.damages = damages;
		this.cooldownShot = cooldownShots;
		
		spawn();
		try {
			laser = new Laser(loc, loc, -1, 200);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
	
	public String getCustomName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Oeil Antique";
	}
	
	public List<ItemStack> getLoots() {
		return Arrays.asList(new ItemBuilder(Material.IRON_NUGGET).setName(ChatColor.GRAY + "" + ChatColor.BOLD + "Pépite mécanique").toItemStack());
	}
	
	public int getXp() {
		return 0;
	}
	
	public void purge() {
		bar.setVisible(false);
		entity.remove();
		if(laser.isStarted())
			laser.stop();
	}
	
	@Override
	public void spawn() {
		if(entity != null)
			if(entity.isValid())
				return;
		entity = (EnderCrystal) loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
		entity.setCustomName(getCustomName());
		entity.setCustomNameVisible(true);
		entity.setGravity(false);
		entity.setShowingBottom(false);
		
		new BukkitRunnable() {
			private long loading = -PERIOD;
			private UUID targetting = UUID.randomUUID();
			@Override
			public void run() {
				if(!entity.isValid()) {
					cancel();
					return;
				}
				entity.setFireTicks(0);
				Player closest = getClosestPlayerAtRange();
				try {
					if(closest != null) {
						final Location cl = closest.getLocation();
						if( ! laser.isStarted()) {
							laser = new Laser(loc, cl, -1, 200);
							laser.start(HalystiaRPG.getInstance());
							//closest.sendMessage("restart laser ->" + laser.isStarted());
						}
						
						if(targetting.equals(closest.getUniqueId())) {
							loading += PERIOD;
							if(loading >= cooldownShot) {
								loading = -PERIOD;
								closest.damage(damages);
								for(Player pl : closest.getWorld().getPlayers()) {
									if(pl.getLocation().distance(cl) < 100) {
										pl.spawnParticle(Particle.EXPLOSION_NORMAL, cl.getX(), cl.getY(), cl.getZ(), 2);
									}
								}
							}
						} else {
							targetting = closest.getUniqueId();
							loading = -PERIOD;
						}
						
						laser.moveEnd(cl);
						
					} else {
						loading = -PERIOD;
						if(laser.isStarted())
							laser.stop();
					}
				} catch (ReflectiveOperationException e) {e.printStackTrace();}
				
				//bar.setTitle(getCustomName() + " - ["+loading+"]");
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 10L, PERIOD);
		
		health = maxHealth;
		updateBar();
	}
	
	private Player getClosestPlayerAtRange() {
		Player p = null;
		double dist = 10000;
		for(Player pl : loc.getWorld().getPlayers()) {
			final Location l = pl.getLocation().add(0, 0.5, 0);
			double distance = l.distance(loc);
			if(distance < range * 1.1) {
				if( ! bar.getPlayers().contains(pl)) {
					bar.addPlayer(pl);
				}
			} else {
				if(bar.getPlayers().contains(pl)) {
					bar.removePlayer(pl);
				}
			}
			if(pl.getGameMode() == GameMode.CREATIVE|| pl.getGameMode() == GameMode.SPECTATOR)
				continue;
			if(distance < range && distance < dist) {
				if(!traceLocation(loc, l, pl)) {
					p = pl;
					dist = distance;
				}
			}
		}
		return p;
	}
	
	public boolean traceLocation(final Location from, final Location to, Player p) {
		Location cl = from.clone();
		double x = to.getX()-from.getX(), y = to.getY()-from.getY(), z = to.getZ()-from.getZ();
		Vector direction = new Vector(x, y, z).normalize();
		//cl = from.clone();
		while(cl.distance(to) > 1 && cl.distance(from) < range) {
			cl = cl.add(direction);
			Block bl = cl.getBlock();
			if(bl.getType() == Material.AIR || bl.getType() == Material.CAVE_AIR)
				continue;

			if (cl.distance(from) > 2.1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getMinutes() {
		return 10;
	}

	@Override
	protected boolean isValid() {
		return entity.isValid();
	}

	@Override
	protected UUID getEntityUUID() {
		return entity.getUniqueId();
	}

	@Override
	protected void killed() {
		entity.remove();
		if(laser.isStarted())
			laser.stop();
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) < 200) {
				pl.spawnParticle(Particle.EXPLOSION_LARGE,
						loc.getX(), loc.getY(), loc.getZ(), 
						3,
						.5, .5, .5,
						.7
				);
				pl.spawnParticle(Particle.DRIP_LAVA,
						loc.getX(), loc.getY(), loc.getZ(), 
						300,
						.5, .5, .5,
						1
				);
				pl.playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 4f, .8f);
			}
		}
	}
	
}
