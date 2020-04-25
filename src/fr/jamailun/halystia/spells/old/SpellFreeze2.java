package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;
import fr.jamailun.halystia.utils.RandomString;

public class SpellFreeze2 extends Spell {
	
	public final static int RANGE_EFFET = 50;
	public final static int RANGE = 9;
	public final static int TEMPS = 15*20;
	
	
	@Override
	public
	boolean cast(Player p) {
		final HalystiaRPG main = HalystiaRPG.getInstance();
		p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, TEMPS/4, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, TEMPS*3, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, TEMPS/2, 50, false, false , true));
		
		PotionEffectType tmp = new PotionEffectType(101) {
			
			@Override
			public boolean isInstant() {
				return false;
			}
			
			@Override
			public String getName() {
				return "test";
			}
			
			@Override
			public double getDurationModifier() {
				return 5;
			}
			
			@Override
			public Color getColor() {
				return Color.RED;
			}
		};
		
		p.getPotionEffect(tmp);
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 8*20, 3,  true, true, true),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 9*20, 20, true, true, true),
			new PotionEffect(PotionEffectType.JUMP, 9*20, 199, false, false, false)
		);
		
		final List<Player> players = getPlayersAroundPlayer(p, RANGE, true);
		
		for(Player pl : players) {
			pl.setFireTicks(0);
			pl.addPotionEffects(effects);
			pl.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, .1f);
			pl.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, .1f);
			spawnParticles(pl.getLocation(), Particle.CLOUD, 50, .5, 1, 1.2);
			pl.sendMessage(ChatColor.BLUE+"Mais quel est ce froid qui s'abat sur vous ?");
		}
		
		HashMap<Location, BlockData> map = new HashMap<>();
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
							map.put(loc, bl.getBlockData());
							allChangedBlocks.add(bl);
						}
					}
				}
			}
		}
		
		for(final Location loc : map.keySet()) {
			
			//final Location loc = bl.getLocation();
			
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
						p.sendBlockChange(loc, map.get(loc));
					}
				}
			}.runTaskLater(main, RandomString.randInt(TEMPS-100, TEMPS*2));
		}

		return true;
	}

	@Override
	public String getName() {
		return "Blizzard arcanique";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public
	int getLevelRequired() {
		return 90;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.BLUE+"C'est dans un froid inhumain");
		lore.add(ChatColor.BLUE+"que tes adversaires, par tes mains");
		lore.add(ChatColor.BLUE+"périront, de toutes parts gelés.");
		lore.add(ChatColor.BLUE+"Remercie ton coeur glacé.");
		lore.add(ChatColor.DARK_RED+""+ChatColor.ITALIC+"Sortilège interdit");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "freeeze2";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 1800;
	}

}
