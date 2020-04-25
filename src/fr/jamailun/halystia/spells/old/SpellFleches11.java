package fr.jamailun.halystia.spells.old;

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

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;
import fr.jamailun.halystia.spells.Invocator;
import fr.jamailun.halystia.utils.RandomString;

public class SpellFleches11 extends InvocationSpell {
	
	public String getStringIdentification() {
		return "flech11";
	}
	
	public final static int VAGUES = 3;
	public final static int FLECHES = 15;
	public final static double RANGE = 6;
	public final static double PORTEE = 50;
	
	
	public boolean cast(final Player p) {
		
		RayTraceResult targetBlockInfo = p.rayTraceBlocks(PORTEE, FluidCollisionMode.NEVER);
		if (targetBlockInfo == null) {
			p.sendMessage(RED + "Ce sort n'a qu'une portée de " + ((int)PORTEE) + " blocs !");
			return false;
		}
		
		final Location loc = targetBlockInfo.getHitBlock().getLocation();
		
		p.sendMessage(GOLD+"Ils ne pourront que fuir !");
		
		
		
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
						
						HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add((Entity)a, p, false, thiis, 3);
						
						new BukkitRunnable() {
							@Override
							public void run() {
								a.remove();
							}
						}.runTaskLater(HalystiaRPG.getInstance(), 20*5L);
						
						spawnParticles(loc, Particle.SMOKE_NORMAL, 50, RANGE/2, RANGE, .8);
						spawnParticles(loc, Particle.FLAME, 2, 0, 0, .1);
						
					}
					
					for(Player pl : getPlayersAroundPlayer(p, 50, true))
						pl.playSound(loc, Sound.ITEM_CROSSBOW_SHOOT, 1.5f, .7f);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), i*60);
		}
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 75;
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
		lore.add(ChatColor.GRAY+"Ils ne sont pas encore prêts...");
		return lore;
	}

	@Override
	public String getName() {
		return "Pluie acérée";
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
		return 180;
	}

}
