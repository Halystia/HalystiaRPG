package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class FrappeTerrestre extends Spell {

	public final static double RANGE = 4.5;
	public final static long PERIOD = 10L, TOTAL_DURATION = 8*20L;
	
	@Override
	public boolean cast(Player p) {
		if(isCasting(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Attends un peu avant de relancer le sort !");
			return false;
		}
		casters.add(p);
		
		new BukkitRunnable() {
			private int count = 0;
			@Override
			public void run() {
				count += PERIOD;
				if(count > TOTAL_DURATION) {
					cancel();
					casters.remove(p);
					return;
				}
				
				double x = p.getLocation().getX();
				double y = p.getLocation().getY();
				double z = p.getLocation().getZ();
				
				for(Entity entity : getEntitiesAroundPlayer(p, 120, false)) {
					if(!(entity instanceof LivingEntity))
						continue;
					
					if(entity instanceof Player) {
						Player pl = (Player) entity;
						pl.playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, 1f, .9f);
						pl.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 30, 0, 0, 0, .2);
					}
					
					if(entity.getLocation().distance(p.getLocation()) <= RANGE) {
						if(entity instanceof Damageable)
							((Damageable)entity).damage(2);
						
						Location loc = ((LivingEntity)entity).getEyeLocation();
						Vector vector = new Vector(loc.getX() - x, loc.getY() - y, loc.getZ() - z);
						vector.multiply(.5);
						entity.setVelocity(vector);
					}
					
				}
				
				p.playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, 1f, .9f);
				p.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 30, 0, 0, 0, .2);
			}
		}.runTaskTimer(main, 0L, PERIOD);
		
		return true;
	}

	@Override
	public String getName() {
		return "Frappe terrestre";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 15;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
				ChatColor.GRAY + "Fait appraitre une zone dans laquelle",
				ChatColor.GRAY + "tout adversaire sera par la terre mordu."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-frappeTerr";
	}

	@Override
	public int getManaCost() {
		return 18;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
