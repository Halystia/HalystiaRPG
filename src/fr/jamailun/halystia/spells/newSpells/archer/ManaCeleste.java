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

public class ManaCeleste extends Spell {
	
	private final List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.SLOW, 9, 8*20, false, false, true));
	public final static int MANA = 10;
	
	@Override
	public boolean cast(Player p) {
		for(Player pl : getPlayersAroundPlayer(p, 10, true)) {
			spawnParticles(pl, p.getLocation(), Particle.FALLING_WATER, 600, 9, .5, .3);
			pl.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 4f, .8f);
			p.sendMessage(ChatColor.GREEN + "Vous sentez un peu de mana affluer en vous.");
		}
		for(PotionEffectType effect : new PotionEffectType[] {PotionEffectType.SLOW, PotionEffectType.SPEED, PotionEffectType.JUMP})
			if(p.getPotionEffect(effect) != null)
				p.removePotionEffect(effect);
		for(PotionEffect effect : effects)
			p.addPotionEffect(effect);
		return true;
	}

	@Override
	public String getName() {
		return "Mana Céleste";
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
		return 10;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Rendez à vous et aux joueurs");
		lore.add(ChatColor.GRAY+"environnants " + ChatColor.AQUA + MANA + ChatColor.GRAY + " points de mana.");
		lore.add(ChatColor.GRAY+ "" + ChatColor.ITALIC + "Attention: vous serez immobilisé temporairement.");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-ManaC";
	}
	
	@Override
	public int getManaCost() {
		return 0;
	}

	@Override
	public int getCooldown() {
		return 8;
	}

}
