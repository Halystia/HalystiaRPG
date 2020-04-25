package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellFireball2 extends Spell {
	
	public String getStringIdentification() {
		return "fb2";
	}
	
	public boolean cast(Player p) {
		Fireball ball = p.launchProjectile(Fireball.class);
		spawnParticles(p.getLocation(), Particle.DRIP_LAVA, 200, 2, 3, 1.3);
		spawnParticles(p.getLocation(), Particle.FLAME, 200, 1, .5, .2);
		ball.setBounce(false);
		ball.setIsIncendiary(true);
		ball.setInvulnerable(false);
		ball.setYield(4);
		p.sendMessage(ChatColor.GOLD + "C'est vous le responsable : vous les avez tous MAUDITS !");
		
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
		}.runTaskTimer(HalystiaRPG.getInstance(), 0L, 5L);
		
		scheduleRemoveEntity(ball, 10);
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 60;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Invoque une terrible boule de feu...");
		lore.add(ChatColor.GRAY+"Les murs de vos enemis ont intéret");
		lore.add(ChatColor.GRAY+"à être solides !");
		return lore;
	}

	@Override
	public String getName() {
		return "Soleil rayonnant";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 100;
	}

}
