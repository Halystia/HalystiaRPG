package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.*;

public class BenedictionNaturelle extends Spell {

	public final static long PERIOD = 8L, TOTAL_TIME = 20*20L;
	
	private List<PotionEffect> effects;
	@Override
	public void init() {
		effects = Arrays.asList(
			new PotionEffect(PotionEffectType.ABSORPTION, 20*35, 1, true, true, true),
			new PotionEffect(PotionEffectType.REGENERATION, 20*25, 1, true, true, true)
		);
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		RayTraceResult rayTrace = p.rayTraceBlocks(5, FluidCollisionMode.NEVER);
		if(rayTrace == null) {
			p.sendMessage(ChatColor.RED + "Il faut viser un joueur pour utiliser ce sort ! Portée maximale de 5 blocs.");
			return false;
		}
		Entity entity = rayTrace.getHitEntity();
		if(entity == null) {
			p.sendMessage(ChatColor.RED + "Il faut viser un joueur pour utiliser ce sort ! Portée maximale de 5 blocs.");
			return false;
		}
		
		if(!(entity instanceof Player)) {
			p.sendMessage(ChatColor.RED + "Il faut viser un joueur pour utiliser ce sort !");
			return false;
		}
		
		Player target = (Player) entity;
		for(PotionEffect eff : effects)
			target.addPotionEffect(eff);
		
		for(Player pl : getPlayersAround(target.getLocation(), 100)) {
			pl.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, .2f, 1.7f);
			spawnParticles(pl, target.getLocation(), Particle.DRAGON_BREATH, 200, .25, .25, .1);
		}
		
		new BukkitRunnable() {
			private long PASSED = 0L;
			@Override
			public void run() {
				if(!target.isValid()) {
					cancel();
					return;
				}
				PASSED += PERIOD;
				spawnParticles(target.getLocation(), Particle.WATER_SPLASH, 30, .2, .2, .2);
				if(PASSED >= TOTAL_TIME) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(main, 0L, PERIOD);
		
		return true;
	}

	@Override
	public String getName() {
		return "Bénédiction naturelle";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 15;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Protégez un joueur allié grâce à ce",
			ChatColor.GRAY + "sort qui saura réchauffer son coeur."
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-beneNatu";
	}

	@Override
	public int getManaCost() {
		return 12;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
