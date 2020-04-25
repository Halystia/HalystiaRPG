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

public class SpellCdReduc0 extends Spell {

	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.FIREWORKS_SPARK, 200, 1, 1, .1);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 2*20, 1,  false, false, true),
			new PotionEffect(PotionEffectType.HUNGER, 4*20, 100,  false, false, true)
		);
		
		p.addPotionEffects(effects);
		HalystiaRPG.getInstance().getSpellManager().substractCooldown(p, 60);
		p.sendMessage(ChatColor.RED+"Vous souffrez... Mais vous avez perdu 60 secondes de cooldown.");
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
		return 30;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Accélère votre âme dans le temps.");
		lore.add(ChatColor.GRAY+"Vous en ressortirez rajeuni de 60 secondes !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "anticd0";
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
