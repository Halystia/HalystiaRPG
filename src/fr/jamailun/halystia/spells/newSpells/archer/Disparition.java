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

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class Disparition extends Spell {

	@Override
	public boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.SMOKE_LARGE, 300, .2, 1, .01);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.INVISIBILITY, 20*20, 0, false, true, true),
			new PotionEffect(PotionEffectType.SPEED, 20*20, 2, false, true, true),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*20, 2, false, true, true),
			new PotionEffect(PotionEffectType.WEAKNESS, 20*20, 3, false, true, true)
		);
		//PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 6*20, 0, true, true, true);

		for(PotionEffect effect : effects)
			p.removePotionEffect(effect.getType());
		p.addPotionEffects(effects);
		
		for(Player pl : getPlayersAroundPlayer(p, 5, true)) {
			//pl.addPotionEffect(blind);
			spawnParticles(p.getLocation(), Particle.SMOKE_LARGE, 20, 0, 0, .8);
			pl.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1f, 1.2f);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Disparition";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Disparaissez, soyez oublié");
		lore.add(ChatColor.GRAY+"durant quelques secondes.");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-dispa";
	}
	
	@Override
	public int getManaCost() {
		return 20;
	}

	@Override
	public int getCooldown() {
		return 20;
	}

}
