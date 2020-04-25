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

public class SpellHeal0 extends Spell {
	
	@Override
	public boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.VILLAGER_HAPPY, 50, .75, 1, .1);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.HEAL, 1, 1, false, false, false)
		);
		
		for(Player pl : getPlayersAroundPlayer(p, 1.5, true)) {
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_WORK_CLERIC, 2f, .1f);
		}
		p.sendMessage(ChatColor.GREEN + "" + "Les soins ont fontionnés :"+ChatColor.DARK_GREEN+" + 4 coeurs"+ChatColor.GREEN+".");
		return true;
	}

	@Override
	public String getName() {
		return "Premiers soins";
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
		return 10;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Un petit sort de soin");
		lore.add(ChatColor.GRAY+"afin d'aider quelques camarades");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "heal0";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 2;
	}

}
