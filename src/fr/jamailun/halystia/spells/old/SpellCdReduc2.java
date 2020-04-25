package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellCdReduc2 extends Spell {

	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.FIREWORKS_SPARK, 200, .5, .5, .1);
		spawnParticles(p.getLocation(), Particle.CLOUD, 200, 2, 2, .1);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 2*20, 1,  false, false, true),
			new PotionEffect(PotionEffectType.HUNGER, 20*6, 100,  false, false, false),
			new PotionEffect(PotionEffectType.WITHER, 20*10, 0,  true, true, true)
		);
		
		p.addPotionEffects(effects);
		HalystiaRPG.getInstance().getSpellManager().substractCooldown(p, 3600);
		p.sendMessage(ChatColor.RED+"En échange de beaucoup de sang, vous avez perdu "+ChatColor.DARK_RED+"une heure"+ChatColor.RED+" de cooldown.");
		return true;
	}

	@Override
	public String getName() {
		return "Accélérateur temporel III";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.LIGHT_PURPLE;
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
		lore.add(ChatColor.GRAY+"Accélère votre âme dans le temps.");
		lore.add(ChatColor.GRAY+"Vous en ressortirez rajeuni de une heure !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "anticd2";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 0;
	}
	
}
