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

public class SpellCdReduc1 extends Spell {

	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.FIREWORKS_SPARK, 200, 1, 1, .1);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 2*20, 1,  false, false, true),
			new PotionEffect(PotionEffectType.HUNGER, 20*4, 100,  false, false, true),
			new PotionEffect(PotionEffectType.HARM, 1, 1,  false, false, true)
		);
		
		p.addPotionEffects(effects);
		HalystiaRPG.getInstance().getSpellManager().substractCooldown(p, 1800);
		p.sendMessage(ChatColor.RED+"En échange d'un peu de sang, vous avez perdu 30 minutes de cooldown.");
		return true;
	}

	@Override
	public String getName() {
		return "Accélérateur temporel I";
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
		return 60;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Accélère votre âme dans le temps.");
		lore.add(ChatColor.GRAY+"Vous en ressortirez rajeuni de 30 minutes !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "anticd1";
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
