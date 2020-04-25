package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.*;

public class JetVolcanique extends Spell {
	
	@Override
	public synchronized boolean cast(Player p) {
		
		Fireball ball = p.launchProjectile(Fireball.class);
		spawnParticles(p.getLocation(), Particle.DRIP_LAVA, 200, .1, 3, 1);
		spawnParticles(p.getLocation(), Particle.FLAME, 200, 1, .5, .2);
		ball.setBounce(false);
		ball.setIsIncendiary(true);
		ball.setInvulnerable(false);
		ball.setYield(3);
		
		for(Player pl : getPlayersAroundPlayer(p, 100, true))
				pl.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_WITHER, 1.5f, .8f);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if( ! ball.isValid()) {
					cancel();
					return;
				}
				spawnParticles(ball.getLocation(), Particle.FLASH, 1, .1, .1, 1);
			}
		}.runTaskTimer(main, 0L, 5L);
		
		scheduleRemoveEntity(ball, 10);
		return true;
	}

	@Override
	public String getName() {
		return "Jet volcanique";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Conjurez les éléments du feu",
			ChatColor.GRAY + "afin de propulser une boule",
			ChatColor.GRAY + "de feu destructrice !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-jetVolca";
	}

	@Override
	public int getManaCost() {
		return 45;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
