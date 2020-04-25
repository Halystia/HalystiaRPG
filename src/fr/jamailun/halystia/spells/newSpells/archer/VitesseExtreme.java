package fr.jamailun.halystia.spells.newSpells.archer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class VitesseExtreme extends Spell {

	public final static int DURATION_SECONDS = 20;
	
	private final List<PotionEffect> effects = Arrays.asList(
				new PotionEffect(PotionEffectType.SPEED, 20*20, 1, true, true, true),
				new PotionEffect(PotionEffectType.FAST_DIGGING, 20*20, 2, true, true, false)
			);
	
	@Override
	public boolean cast(final Player p) {
		
		for(PotionEffect effect : effects)
			p.removePotionEffect(effect.getType());
		p.addPotionEffects(effects);
		
		double newHealth = p.getHealth() - 2;
		p.setHealth(newHealth);
		
		new BukkitRunnable() {
			private int count = DURATION_SECONDS;
			@Override
			public void run() {
				if(p.isDead() || count <= 0) {
					cancel();
					return;
				}
				if(p.getPotionEffect(PotionEffectType.SPEED) == null && p.getPotionEffect(PotionEffectType.FAST_DIGGING) == null) {
					cancel();
					return;
				}
				spawnParticles(p.getLocation(), Particle.WATER_SPLASH, 50, .5, 1, .4);
				for(Player pl : getPlayersAroundPlayer(p, 40, true))
					pl.playSound(p.getLocation(), Sound.ENTITY_FISH_SWIM, 3f, .5f);
				count--;
			}
		}.runTaskTimer(main, 0L, 20L);
		
		return true;
	}
	
	@Override
	public String getName() {
		return "Vitesse Extrème";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_BLUE;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public int getLevelRequired() {
		return 30;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"C'est le moment d'accélérer !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-vitExt";
	}
	
	@Override
	public int getManaCost() {
		return 15;
	}

	@Override
	public int getCooldown() {
		return 8;
	}
	
}
