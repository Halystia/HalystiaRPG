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

public class SpellDisparition extends Spell {

	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.SMOKE_LARGE, 300, .2, 1, .01);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.INVISIBILITY, 20*20, 0, false, false, true),
			new PotionEffect(PotionEffectType.JUMP, 10*20, 4, false, false, true),
			new PotionEffect(PotionEffectType.SPEED, 5*20, 9, false, false, true),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2*15, 50, false, false, true)
		);
		PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 6*20, 0, true, true, true);

		for(PotionEffect effect : effects)
			p.removePotionEffect(effect.getType());
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Disparitiooooon !");
		p.addPotionEffects(effects);
		
		for(Player pl : getPlayersAroundPlayer(p, 5, false)) {
			pl.addPotionEffect(blind);
			pl.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1f, 1.2f);
		}
		p.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1f, 1.2f);
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
	public
	Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public
	int getLevelRequired() {
		return 50;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Disparaissez, soyez oublié");
		lore.add(ChatColor.GRAY+"durant quelques secondes");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "dispa0";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 120;
	}

}
