package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.*;

public class ProtectionElementaire extends Spell {

	public final static Set<UUID> players = new HashSet<>();
	
	@Override
	public synchronized boolean cast(final Player p) {
		UUID uuid = p.getUniqueId();
		if(players.contains(uuid)) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "La bénédiction est déjà en cours.");
			return false;
		}
		
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.BLUE + "Le prochain coup reçu (pendant 20s) sera annulé.");
		for(Player pl : getPlayersAroundPlayer(p, 100, true))
			spawnParticles(pl, p.getLocation(), Particle.DRIPPING_HONEY, 300, .2, 1, .05);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(players.contains(uuid)) {
					players.remove(uuid);
					p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "La bénédiction a pris fin.");
				}
			}
		}.runTaskLater(main, 20*20L);
		
		return true;
	}

	@Override
	public String getName() {
		return "Protection élémentaire";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.BLUE;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 25;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Durant 20 secondes, bénédiction :",
			ChatColor.GRAY + "prochain dégat annulé."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-protecElem";
	}

	@Override
	public int getManaCost() {
		return 20;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
