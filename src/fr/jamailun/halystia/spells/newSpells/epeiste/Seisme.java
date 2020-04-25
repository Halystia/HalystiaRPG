package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class Seisme extends Spell {

	private PotionEffect slow;
	
	public final static double RANGE = 10;
	public final static double DAMAGES = 5;
	public final static int WAVES = 3;
	public final static int DELAY = 3;
	
	@Override
	public void init() {
		slow = new PotionEffect(PotionEffectType.SLOW, DELAY*(WAVES)+20, 50);
	}
	
	@Override
	public boolean cast(Player p) {
		p.addPotionEffect(slow);
		new BukkitRunnable() {
			private boolean oneOfTwo = false;
			private int waves = WAVES;
			@Override
			public void run() {
				if( (! p.isValid()) || waves <= 0) {
					cancel();
					return;
				}
				final Location loc = p.getLocation();
				for(Player pl : getPlayersAround(loc, 50)) {
					pl.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 5f, .4f);
					pl.spawnParticle(Particle.CRIT, loc, 400, RANGE/2, .1, RANGE/2, .5);
				}
				if(!oneOfTwo) {
					oneOfTwo = true;
					return;
				}
				oneOfTwo = false;

				p.setVelocity(new Vector(0, 2, 0));
				
				new BukkitRunnable() {
					@Override
					public void run() {
						p.setVelocity(new Vector(0, -4, 0));
					}
				}.runTaskLater(main, 3L);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Entity en : getEntitiesAroundPlayer(p, RANGE, false)) {
							if(en instanceof LivingEntity) {
								((LivingEntity)en).damage(DAMAGES, p);
							}
						}
						spawnParticles(p.getLocation(), Particle.SQUID_INK, 200, RANGE/2, .2, .1);
						playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 10f, .2f);
					}
				}.runTaskLater(main, 8L);
				
				waves--;
			}
		}.runTaskTimer(main, 2L, DELAY * 10L);
		
		return true;
	}

	@Override
	public String getName() {
		return "Séisme";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 10;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Faites trembler le sol,",
			ChatColor.GRAY + "Faites trembler vos adversaires.",
			ChatColor.GRAY + "Faites trembler leurs convictions."
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-seisme";
	}

	@Override
	public int getManaCost() {
		return 9;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
