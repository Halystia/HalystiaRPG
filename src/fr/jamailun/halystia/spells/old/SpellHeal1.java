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

public class SpellHeal1 extends Spell {
	
	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.VILLAGER_HAPPY, 150, .75, 1, .1);
		spawnParticles(p.getLocation(), Particle.SPELL_INSTANT, 50, .5, .5, .4);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.HEAL, 1, 3, false, false, false),
			new PotionEffect(PotionEffectType.ABSORPTION, 1, 20*20, false, false, false)
		);
		
		for(Player pl : getPlayersAroundPlayer(p, 2, true)) {
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_WORK_CLERIC, 2f, .1f);
		}
		p.sendMessage(ChatColor.GREEN + "" + "Les soins ont fontionnés :"+ChatColor.DARK_GREEN+" + 12 coeurs"+ChatColor.GREEN+".");
		return true;
	}

	@Override
	public String getName() {
		return "Soins avancés";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GREEN;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.NONE;
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
		lore.add(ChatColor.GRAY+"Un sort de soin assez difficile,");
		lore.add(ChatColor.GRAY+"mais était d'une grand utilité");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "heal1";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 45;
	}

}
