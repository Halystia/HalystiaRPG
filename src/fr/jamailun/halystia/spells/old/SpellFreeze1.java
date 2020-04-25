package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;
import fr.jamailun.halystia.utils.RandomString;

public class SpellFreeze1 extends Spell {
	
	public final static int RANGE = 5;
	public final static int TEMPS = 5*20;
	
	@Override
	public
	boolean cast(Player p) {
		final HalystiaRPG main = HalystiaRPG.getInstance();
		spawnParticles(p.getLocation(), Particle.ENCHANTMENT_TABLE, 100, 1, 1, .1);
		spawnParticles(p.getLocation(), Particle.FLASH, 5, 1, 1, .05);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 8*20, 1,  true, true, true),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 9*20, 9, true, true, true)
		);
		
		final List<Player> players = getPlayersAroundPlayer(p, RANGE, true);
		
		for(Player pl : players) {
			pl.setFireTicks(0);
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 2f, .1f);
			pl.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, .5f);
			spawnParticles(pl.getLocation(), Particle.CLOUD, 50, .5, 1, 1.2);
		}
		

		List<Block> allChangedBlocks = new ArrayList<>();
		// Tous les blocks à changer.
		final Block b = p.getLocation().getBlock();
		for(int x = b.getX() - RANGE; x <= b.getX() + RANGE; x++) {
			for(int y = b.getY() - RANGE; y <= b.getY() + RANGE; y++) {
				for(int z = b.getZ() - RANGE; z <= b.getZ() + RANGE; z++) {
					final Location loc = b.getWorld().getBlockAt(x, y, z).getLocation();
					if(b.getLocation().distance(loc) <= RANGE) {
						final Block bl = b.getWorld().getBlockAt(loc);
						if(bl.getType() != Material.AIR && bl.getType() != Material.CAVE_AIR) {
							allChangedBlocks.add(bl);
						}
					}
				}
			}
		}
		
		for(final Block bl : allChangedBlocks) {
			
			final Location loc = bl.getLocation();
			
			//1 : on change le block coté client
			new BukkitRunnable() {
				@Override
				public void run() {
					spawnParticles(loc, Particle.WATER_SPLASH, 40, 1, 1, .8);
					for(Player p : players) {
						p.sendBlockChange(loc, main.getServer().createBlockData(Material.PACKED_ICE));
					}
				}
			}.runTaskLater(main, RandomString.randInt(0, 50));
			
			//2 on remet le block comme il est vraiment !
			
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player p : players) {
						p.sendBlockChange(loc, bl.getBlockData());
					}
				}
			}.runTaskLater(main, RandomString.randInt(TEMPS-100, TEMPS*2));
		}

		return true;
	}

	@Override
	public String getName() {
		return "Souffle glacial";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.AQUA;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public
	int getLevelRequired() {
		return 50;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Refrodit ces horribles ennemis !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "freeeze1";
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
