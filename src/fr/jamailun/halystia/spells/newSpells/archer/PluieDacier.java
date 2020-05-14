package fr.jamailun.halystia.spells.newSpells.archer;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;
import fr.jamailun.halystia.spells.Invocator;
import fr.jamailun.halystia.utils.RandomString;

public class PluieDacier extends InvocationSpell {
	
	public String getStringIdentification() {
		return "a-PluieAcier";
	}
	
	public final static int VAGUES = 3;
	public final static int FLECHES = 60;
	public final static double RANGE = 6;
	public final static double PORTEE = 40;
	public final static int PERIOD = 1;
	
	public boolean cast(final Player p) {
		
		RayTraceResult targetBlockInfo = p.rayTraceBlocks(PORTEE, FluidCollisionMode.NEVER);
		if (targetBlockInfo == null) {
			p.sendMessage(RED + "Ce sort n'a qu'une portée de " + ((int)PORTEE) + " blocs !");
			return false;
		}
		
		final Location loc = targetBlockInfo.getHitBlock().getLocation();
		
		final Invocator thiis = this;
		for(int i = 0; i < VAGUES; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(int j = 0; j < FLECHES; j++) {
						double dx = Math.sin(RandomString.randInt(-100, 100)) * Math.random() * RANGE;
						double dz = Math.sin(RandomString.randInt(-100, 100)) * Math.random() * RANGE;
						
						Arrow a = loc.getWorld().spawnArrow(new Location(loc.getWorld(), loc.getX() + dx, loc.getY() + 12, loc.getZ() + dz), new Vector(0, -1, 0), 0.7f, 12);
						
						a.setCustomNameVisible(false);
						a.setShooter(p);
						
						main.getSpellManager().getInvocationsManager().add((Entity)a, p, false, thiis, 3);
						
						new BukkitRunnable() {
							@Override
							public void run() {
								a.remove();
							}
						}.runTaskLater(main, 20*3L);
						
						spawnParticles(loc, Particle.SMOKE_NORMAL, 50, RANGE/2, 2, .05);
						spawnParticles(loc, Particle.FLAME, 2, 0, 0, .1);
						
					}
					
					for(Player pl : getPlayersAroundPlayer(p, 80, true))
						pl.playSound(loc, Sound.ITEM_CROSSBOW_SHOOT, 1.5f, .7f);
				}
			}.runTaskLater(main, i*20*PERIOD);
		}
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 15;
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
		lore.add(ChatColor.GRAY+"Invoque une pluie de flèches acérées...");
		lore.add(ChatColor.GRAY+"directement sur vos adversaires !");
		lore.add(ChatColor.GRAY+"Ils ne sont pas encore prêts.");
		return lore;
	}

	@Override
	public String getName() {
		return "Pluie d'acier";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public
	int getManaCost() {
		return 12;
	}

	@Override
	public
	int getCooldown() {
		return PERIOD*VAGUES;
	}

}
