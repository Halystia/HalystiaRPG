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

public class SpellSuperBuff extends Spell {
	
	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.DRAGON_BREATH, 900, 1.5, 3, .1);
		spawnParticles(p.getLocation(), Particle.FLASH, 100, .1, .1, 1);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60*20, 1, true, true, true),
			new PotionEffect(PotionEffectType.SPEED, 60*20, 2, true, true, true),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60*20, 0, true, true, true),
			new PotionEffect(PotionEffectType.REGENERATION, 60*20, 0, true, true, true)
		);
		
		for(Player pl : getPlayersAroundPlayer(p, 3, true)) {
			for(PotionEffect effect : effects)
				pl.removePotionEffect(effect.getType());
			pl.addPotionEffects(effects);
			pl.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Tu reçois un effet de " + getColor() + getName() + ChatColor.GOLD + "" + ChatColor.BOLD + " !");
			pl.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_ENDER_DRAGON, 2f, .1f);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Bénédiction élémentaire";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_BLUE;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public
	int getLevelRequired() {
		return 60;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Invoque les éléments afin de");
		lore.add(ChatColor.GRAY+"conjurer une puissante bénédiction");
		lore.add(ChatColor.GRAY+"juste autour de vous");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "bene10";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 30;
	}

}
