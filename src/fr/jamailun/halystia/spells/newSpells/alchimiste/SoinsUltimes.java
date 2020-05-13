package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.*;

public class SoinsUltimes extends Spell {
	
	public final static int RANGE = 5;
	public final static int VAGUES = 3;
	public final static int DELAI = 20*3;
	public final static int HEALTH = 8;
	
	@Override
	public synchronized boolean cast(Player p) {
		for(int i = 0; i < VAGUES; i ++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Entity e : getEntitiesAroundPlayer(p, 100, true)) {
						if( ! (e instanceof LivingEntity))
							return;
						boolean heal = e.getLocation().distance(p.getLocation()) < RANGE;
						
						if(e instanceof Player) {
							Player pl = (Player) e;
							pl.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_PLACE, 2f, .4f);
							spawnParticles(pl, p.getLocation(), Particle.HEART, (int) (Math.PI*RANGE*RANGE*6), RANGE, 1, .05);
							spawnParticles(pl, p.getLocation(), Particle.VILLAGER_HAPPY, (int) (Math.PI*RANGE*RANGE), RANGE, 1, .5);
							if(heal) {
								pl.playSound(pl.getLocation(), Sound.BLOCK_WET_GRASS_STEP, 1f, .4f);
								pl.setHealth(pl.getHealth() + HEALTH);
							}
						} else {
							if(heal)
								((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
						}
					}
				}
			}.runTaskLater(main, i*20*3);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Soins primaires";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GREEN;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Un sort de soin extrèmement puissant.",
			ChatColor.GRAY + "En plus de soigner vos alliés, il est",
			ChatColor.GRAY + "efficace contre les morts-vivants !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-soinUtl";
	}

	@Override
	public int getManaCost() {
		return 25;
	}

	@Override
	public int getCooldown() {
		return 4;
	}

}
