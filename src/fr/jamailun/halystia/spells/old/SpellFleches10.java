package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;
import fr.jamailun.halystia.spells.Invocator;
import fr.jamailun.halystia.utils.RandomString;

public class SpellFleches10 extends InvocationSpell {
	
	public String getStringIdentification() {
		return "flech10";
	}
	
	public final static int VAGUES = 3;
	public final static int FLECHES = 20;
	public final static double RANGE = 6;
	
	public boolean cast(final Player p) {
		
		final Invocator thiis = this;
		for(int i = 0; i < VAGUES; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					final Location loc = p.getLocation();
					for(int j = 0; j < FLECHES; j++) {
						double dx = Math.sin(RandomString.randInt(-100, 100)) * Math.random() * RANGE;
						double dz = Math.sin(RandomString.randInt(-100, 100)) * Math.random() * RANGE;
						
						Arrow a = loc.getWorld().spawnArrow(new Location(loc.getWorld(), loc.getX() + dx, loc.getY() + 8, loc.getZ() + dz), new Vector(0, -1, 0), 0.7f, 12);
						
						a.setCustomName(p.getName()+"#4");
						
						HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add((Entity)a, p, false, thiis, 4);
						
						a.setCustomNameVisible(false);
						new BukkitRunnable() {
							@Override
							public void run() {
								a.remove();
							}
						}.runTaskLater(HalystiaRPG.getInstance(), 20*6L);
					}
					
					for(Player pl : getPlayersAroundPlayer(p, 50, true))
						pl.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.5f, .7f);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), i*60);
		}
		
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 40;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Invoque une pluie  de flèches acérées.");
		lore.add(ChatColor.GRAY+"3 vagues devraient suffire à les contenir !");
		return lore;
	}

	@Override
	public String getName() {
		return "Pluie d'acier";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GRAY;
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
