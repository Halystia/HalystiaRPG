package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.*;
import fr.jamailun.halystia.utils.Laser;

public class Etincelle extends Spell {

	public final static int RANGE = 25;
	public final static int TICK_DURATION = 20;
	public final static int PERIOD = 2;
	
	@Override
	public synchronized boolean cast(final Player p) {
		if(isCasting(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Attends un peu avant de relancer le sort !");
			return false;
		}
		casters.add(p);
		
		final Location source = p.getLocation();
		
		double min = RANGE*2;
		LivingEntity cible = null;
		for(Entity en : getEntitiesAroundPlayer(p, RANGE, false)) {
			if(!(en instanceof LivingEntity))
				continue;
			if(en.getType() == EntityType.VILLAGER)
				continue;
			double dist = en.getLocation().distance(source);
			if(dist < min) {
				min = dist;
				cible = (LivingEntity) en;
			}
		}
		if(cible == null) {
			p.sendMessage(ChatColor.RED + "Aucune entité dans un rayon de " + RANGE + " blocs n'a été trouvé.");
			return false;
		}
		
		try {
			final Laser laser = new Laser(source.add(0, p.getEyeHeight()/2, 0), cible.getLocation().add(0, cible.getEyeHeight()/2, 0), 10, 60);
			
			final LivingEntity target = cible;
			
			laser.start(HalystiaRPG.getInstance());
			new BukkitRunnable() {
				private int tick = 0;
				@Override
				public void run() {
					
					try {
						laser.moveStart(p.getLocation().add(0, p.getEyeHeight()/2, 0));
						laser.moveEnd(target.getLocation().add(0, target.getEyeHeight()/2, 0));
					} catch (ReflectiveOperationException e) {
						e.printStackTrace();
					}
					
					target.damage(3);
					
					if( ! (target.isValid() && p.isValid()))
						tick = TICK_DURATION;
					
					tick++;
					if(tick > TICK_DURATION) {
						casters.remove(p);
						cancel();
						laser.stop();
					}
				}
			}.runTaskTimer(HalystiaRPG.getInstance(), 0L, PERIOD);
			
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

	@Override
	public String getName() {
		return "Étincelle";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Attaque brièvement l'entité",
			ChatColor.GRAY + "le plus proche de vous à l'aide",
			ChatColor.GRAY + "d'un rayon destructeur."
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-etincelle";
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
