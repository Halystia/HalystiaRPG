package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellSilence1 extends Spell {
	
	@Override
	public
	boolean cast(Player p) {
		spawnParticles(p.getLocation(), Particle.ENCHANTMENT_TABLE, 500, 1, 1, .1);
		spawnParticles(p.getLocation(), Particle.FLASH, 5, 1, 1, .05);

		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1*20, 50, true, true, true));
		
		LightningStrike strike = p.getWorld().spawn(p.getLocation(), LightningStrike.class);
		strike.setGlowing(false);
		
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 6*20, 0, true, true, true));
		p.setFireTicks(0);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 2*20, 50,  false, false, false),
			new PotionEffect(PotionEffectType.BLINDNESS, 2*20, 50, false, false, false)
		);
		int n = 0;
		for(Player pl : getPlayersAroundPlayer(p, 50, false)) {
			
			for(PotionEffectType type : PotionEffectType.values())
				if(type != PotionEffectType.GLOWING)
					pl.removePotionEffect(type);
			pl.setFireTicks(0);
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2f, .1f);
			pl.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+""+ChatColor.ITALIC+" SILENCE !");
			spawnParticles(pl.getLocation(), Particle.EXPLOSION_HUGE, 2, 1, 1, .8);
			n++;
		}
		
		p.sendMessage(ChatColor.AQUA+(n==0 ? "Personne n'a été réduit" : n+(n>1?" personnes ont été réduites" : " personne a été réduite")) + " au silence.");
		return true;
	}

	@Override
	public String getName() {
		return "Taisez vous !";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.AQUA;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
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
		lore.add(ChatColor.GRAY+"Réduit au "+ChatColor.AQUA+"silence"+ChatColor.GRAY+" toute personne");
		lore.add(ChatColor.GRAY+"dans un rayon de 50 mètres.");
		lore.add(ChatColor.GRAY+"Vous serez le seul à être puissant !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "silence0";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 200;
	}

}
