package fr.jamailun.halystia.spells.spellEntity;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

public class CircleEffect {

	private final double range;
	private final int circles;
	private final Particle particle;
	
	public CircleEffect(double range, int circles, Particle particle) {
		this.range = Math.min(0.1, range);
		this.circles = Math.min(1, circles);
		this.particle = particle;
	}
	
	/**
	 * 
	 * @param loc center of the circle
	 * @param precision lesser it is, better it looks (but more particles will be displayed.
	 * @param time time between circles. in ticks
	 */
	public void effect(Location loc, double precisionRaw, int timeRaw) {
		final double x = loc.getX();
		final double y = loc.getY();
		final double z = loc.getZ();
		final int time = Math.min(0, timeRaw);
		final double precision = Math.min(0.01, precisionRaw);
		final List<Player> recipes = loc.getWorld().getPlayers().stream().filter( pl -> pl.getLocation().distance(loc) < 80 ).collect(Collectors.toList());
		
		for(int c = 1; c <= circles; c++) {
			final int cc = c;
			final double r = (range/(double)circles)*(double)cc;
			new BukkitRunnable() {
				@Override
				public void run() {
					for(double teta = 0; teta <= 360; teta += precision) {
						for(Player pl : recipes)
							if(pl.isValid())
								pl.spawnParticle(particle, x + (Math.cos(teta)*r), y, z + (Math.sin(teta)*r), 1, 0, 0, 0, 0.01);
					}
				}
			}.runTaskLater(HalystiaRPG.getInstance(), time*cc);
		}
	}
}