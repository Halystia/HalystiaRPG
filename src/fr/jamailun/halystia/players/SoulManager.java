package fr.jamailun.halystia.players;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.ItemBuilder;

public class SoulManager {
	
	public final static int SECONDS_BETWEEN_REFRESH = 60*20;
	
	private final ItemBuilder soul3, soul2, soul1, soul0;
	private final List<ItemStack> customs;
	private final HashMap<Integer, ItemStack> levels;
	
	private final HalystiaRPG main;
	
	public SoulManager(HalystiaRPG main) {
		this.main = main;
		soul3  = new ItemBuilder(Material.NETHER_STAR, 3).setName(AQUA +"" + BOLD + "Âmes : " + GREEN + "" + BOLD + "3")
				.setLore(GRAY+""+ITALIC+"Lors de ta mort, tu ne perdras " + GREEN + "aucun" + GRAY + "" + ITALIC+" stuff.");
		soul2  = new ItemBuilder(Material.NETHER_STAR, 2).setName(AQUA +"" + BOLD + "Âmes : " + YELLOW + "" + BOLD + "2")
				.setLore(GRAY+""+ITALIC+"Lors de ta mort, tu perdras " + YELLOW + "10%" + GRAY + "" + ITALIC+" de ton stuff.");
		soul1 = new ItemBuilder(Material.NETHER_STAR, 1).setName(AQUA +"" + BOLD + "Âme : " + RED + "" + BOLD + "1")
				.setLore(GRAY+""+ITALIC+"Lors de ta mort, tu perdras " + RED + "30%" + GRAY + "" + ITALIC+" de ton stuff.");
		soul0 = new ItemBuilder(Material.STRUCTURE_VOID, 1).setName(AQUA +"" + BOLD + "Âme : " + DARK_RED + "" + BOLD + "aucune")
				.setLore(GRAY+""+ITALIC+"Lors de ta mort, tu perdras " + DARK_RED + "50%" + GRAY + "" + ITALIC+" de ton stuff.");
		
		levels = new HashMap<>();
		levels.put(0, soul0.toItemStack());
		levels.put(1, soul1.toItemStack());
		levels.put(2, soul2.toItemStack());
		levels.put(3, soul3.toItemStack());
		
		customs = Arrays.asList(soul0.toItemStack(), soul1.toItemStack(), soul2.toItemStack(), soul3.toItemStack());
	}
	
	public ItemStack getItemForPlayer(Player p) {
		int soulLevel = main.getDataBase().getHowManySouls(p);
		if(soulLevel < 0)
			soulLevel = 0;
		if(soulLevel > 3)
			soulLevel = 3;
		return new ItemStack(levels.get(soulLevel));
	}
	
	public double stuffLost(Player p) {
		int level = main.getDataBase().getHowManySouls(p);
		switch(level) {
			case 0:
				return 0.5;
			case 1:
				return 0.3;
			case 2:
				return 0.1;
		}
		return 0;
	}
	
	public boolean isSoulObject(ItemStack item) {
		for(ItemStack custom : customs)
			if(Trade.areItemsTheSame(item, custom))
				return true;
		return false;
	}
	
	public boolean tryRefreshSoul(Player p) {
		int souls = main.getDataBase().getHowManySouls(p);
		if(souls >= 3)
			return false;
		
		int secondes = main.getDataBase().getLastSoulRefresh(p);
		if(secondes < SECONDS_BETWEEN_REFRESH)
			return false;
		boolean canRefresh = false;
		
		while(secondes >= SECONDS_BETWEEN_REFRESH && souls < 3) {
			canRefresh = true;
			secondes -= SECONDS_BETWEEN_REFRESH;
			main.getDataBase().refreshSoul(p);
			souls++;
		}
		
		if(!canRefresh)
			return false;
		
	//	setSouls(p);
		
		p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Tu as régénéré une âme ! Tu perdras moins de stuff en cas de mort !");
		
		return true;
	}
	
	private boolean running = false;
	public void startClock() {
		if(running)
			return;
		running = true;
		final World world = Bukkit.getWorld(HalystiaRPG.WORLD);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			@Override
			public void run() {
				for(Player p : world.getPlayers()) {
					tryRefreshSoul(p);
				}
			}
		}, 10*20L, 20*10L);
	}	
}