package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs2.JobResult;
import fr.jamailun.halystia.jobs2.JobsManager;

public class PlayerBreakListener extends HalystiaListener {

	private final JobsManager jobs;
	public PlayerBreakListener(HalystiaRPG main, JobsManager jobs) {
		super(main);
		this.jobs = jobs;
	}
	
	@EventHandler
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
		
		JobResult result = jobs.blockBreakEvent(e.getBlock(), p);
		
		switch ( result.getType() ) {
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