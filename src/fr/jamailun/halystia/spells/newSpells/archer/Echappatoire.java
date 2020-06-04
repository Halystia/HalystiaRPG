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

public class Echappatoire extends Spell {
	
	private final List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 8*20, 0, true, true, true));
	
	@Override
	public boolean cast(Player p) {
		
		for(Player pl : getPlayersAroundPlayer(p, 5, true)) {
			super.spawnParticles(pl, p.getLocation(), Particle.END_ROD, 500, 4, .5, .8);
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 4f, .8f);
			p.sendMessage(ChatColor.GREEN + "Vous vous sentez plus léger.");
		}
		return true;
	}

	@Override
	public String getName() {
		return "Échappatoire";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GREEN;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public int getLevelRequired() {
		return 1;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Offrez un peu de vitesse");
		lore.add(ChatColor.GRAY+"dans la vie de vos alliés.");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-Echapp";
	}
	
	@Override
	public int getManaCost() {
		return 12;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
