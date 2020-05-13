package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SoinsPrimaires extends Spell {
	
	public final static int RANGE = 3;
	public final static int VAGUES = 2;
	public final static int DELAI = 20*3;
	public final static int HEALTH = 4;
	
	@Override
	public synchronized boolean cast(Player p) {
		for(int i = 0; i < VAGUES; i ++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player pl : getPlayersAroundPlayer(p, 100, true)) {
						if(pl.getLocation().distance(p.getLocation()) < RANGE)
							pl.setHealth(pl.getHealth() + HEALTH);
						pl.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_PLACE, 2f, .4f);
						spawnParticles(pl, p.getLocation(), Particle.HEART, (int) (Math.PI*RANGE*RANGE*5), RANGE, 1, .05);
					}
				}
			}.runTaskLater(main, i*20*3);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Soins primaires";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GREEN;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 1;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Un sort de soin certes basique",
			ChatColor.GRAY + "mais à ne pas pas sous-estimer !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-soinPrim";
	}

	@Override
	public int getManaCost() {
		return 3;
	}

	@Override
	public int getCooldown() {
		return 4;
	}

}
