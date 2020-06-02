package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SoinsPerfectionnes extends Spell {
	
	public final static int RANGE = 4;
	public final static int VAGUES = 2;
	public final static int DELAI = 20*3;
	public final static int HEALTH = 8;
	
	@Override
	public synchronized boolean cast(Player p) {
		for(int i = 0; i < VAGUES; i ++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player pl : getPlayersAroundPlayer(p, 100, true)) {
						if(pl.getLocation().distance(p.getLocation()) < RANGE) {
							double health = pl.getHealth() + HEALTH;
							double max = pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
							pl.setHealth(health > max ? max : health);
						}
						spawnParticles(pl, p.getLocation(), Particle.HEART, (int) (Math.PI*RANGE*RANGE), RANGE, 1, .05);
					}
				}
			}.runTaskLater(main, i*20*3);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Soins perfectionnés";
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
		return 10;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Un sort de soin plus avancé,",
			ChatColor.GRAY + "pour vous soigner durant les",
			ChatColor.GRAY + "moments difficiles."
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-soinsPerf";
	}

	@Override
	public int getManaCost() {
		return 20;
	}

	@Override
	public int getCooldown() {
		return 6;
	}

}
