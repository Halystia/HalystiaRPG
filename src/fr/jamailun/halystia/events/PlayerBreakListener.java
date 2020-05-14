package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.Equipment;
import fr.jamailun.halystia.jobs.JobResult;
import fr.jamailun.halystia.jobs.JobsManager;

public class PlayerBreakListener extends HalystiaListener {

	private final JobsManager jobs;
	public PlayerBreakListener(HalystiaRPG main, JobsManager jobs) {
		super(main);
		this.jobs = jobs;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerBreakBlock(BlockBreakEvent e) {
		if( ! HalystiaRPG.isRpgWorld(e.getBlock().getWorld()))
			return;
		Player p = e.getPlayer();
		
		if(main.getMobSpawnerManager().getSpawner(e.getBlock().getLocation()) != null) {
			if(p.getGameMode() == GameMode.CREATIVE)
				p.sendMessage(RED + "Utilise "+DARK_RED+"/set-spawner remove"+RED+" pour supprimer un spawner !");
			e.setCancelled(true);
			return;
		}
		
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		
		final Location loc = e.getBlock().getLocation();
		if(main.getChunkManager().isBuildable(loc)) {
			e.setCancelled(true);
			e.setDropItems(false);
			e.setExpToDrop(0);
			ItemStack tool = p.getInventory().getItemInMainHand();
			for(ItemStack item : e.getBlock().getDrops(tool, p))
				loc.getWorld().dropItemNaturally(loc, item);
			e.getBlock().setType(Material.AIR);
			if(tool != null) {
				if(Equipment.TOOLS.hasObject(tool.getType())) {
					short dura = (short) (tool.getDurability() + 1);
					if( dura < tool.getType().getMaxDurability() ) {
						tool.setDurability(dura);
					} else {
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
						p.getInventory().getItemInMainHand().setAmount(0);
					}
				}
			}
			return;
		}
		
		JobResult result = jobs.blockBreakEvent(e.getBlock(), p);
		
		switch ( result.getType() ) {
			case NOT_HARVESTABLE:
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Impossible de récolter dans cette zone.");
				e.setCancelled(true);
				return;
			case NO_JOB:
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas le bon métier !");
				e.setCancelled(true);
				return;
			case NO_LEVEL:
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas le bon niveau !");
				e.setCancelled(true);
				return;
			case NO_BLOCK:
				e.setCancelled(true);
				return;
			case SUCCESS:
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), result.getData().getLoot());
				restaureBlock(e.getBlock().getLocation(), result.getData().getType(), result.getData().getRespawnTime());
				return;
		}
		
	}
	
	private void restaureBlock(Location loc, Material type, int sec) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Block b = loc.getBlock();
				b.setType(type);
				if(b.getBlockData() instanceof Ageable) {
					Ageable age = (Ageable) b.getBlockData();
					age.setAge(((Ageable)b.getBlockData()).getMaximumAge());
					b.setBlockData(age);
					b.getState().update();
				}
				main.getCache().removeFromBlocksCache(loc);
			}
		}.runTaskLater(main, sec*20L);
		main.getCache().addToBlocksCache(loc, type);
	}
}