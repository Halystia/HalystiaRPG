package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellHeal2 extends Spell {
	
	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.VILLAGER_HAPPY, 800, 2.5, 2.5, .1);
		spawnParticles(p.getLocation(), Particle.SPELL_INSTANT, 150, 2, 2, .5);
		spawnParticles(p.getLocation(), Particle.WATER_SPLASH, 50, 1, 1, 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 0, 20*30));
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 0, 8*30));
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.HEAL, 1, 50, false, false, false),
			new PotionEffect(PotionEffectType.SATURATION, 1, 50, true, true, true),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*30, 0, true, true, true),
			new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*30, 1, true, true, true),
			new PotionEffect(PotionEffectType.REGENERATION, 20*30, 1, true, true, true),
			new PotionEffect(PotionEffectType.ABSORPTION, 60*20, 1, true, true, true),
			new PotionEffect(PotionEffectType.SLOW, 5*20, 0, true, true, true)
		);
		
		for(Player pl : getPlayersAroundPlayer(p, 5, true)) {
			for(PotionEffect effect : effects)
				pl.removePotionEffect(effect.getType());
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2f, .4f);
			pl.setFireTicks(0);
		}
		p.sendMessage(ChatColor.GREEN + "" + "Les soins avancés ont fontionnés.");
		return true;
	}

	@Override
	public String getName() {
		return "Purification";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.NONE;
	}

	@Override
	public
	int getLevelRequired() {
		return 90;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Un sort de soin très puissant.");
		lore.add(ChatColor.GRAY+"Il purifie totalement avant de soigner.");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "heal2";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 60;
	}

}
