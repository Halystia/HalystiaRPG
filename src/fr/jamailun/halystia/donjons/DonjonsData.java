package fr.jamailun.halystia.donjons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;
import fr.jamailun.halystia.utils.ItemBuilder;

public class DonjonsData {
	
	public List<Donjon> getContent() {
		List<Donjon> list = new ArrayList<Donjon>();
		list.add(new Donjon() {
			@Override
			public String getWorldName() {
				return "donjon_rpg";
			}
			
			@Override
			public String getName() {
				return "Donjon de Némésia";
			}
			
			@Override
			public ItemStack getKeyNeed() {
				return new ItemBuilder(Material.TRIPWIRE_HOOK).setName(Rarity.RARE.getColor() + "Clef du " + ChatColor.RED + "" + ChatColor.BOLD + getName()).toItemStack();
			}
			
			@Override
			public Location getEntryInDonjon() {
				return new Location(Bukkit.getWorld(getWorldName()), 193.5, 19, 142.5, -90, 0);
			}

			@Override
			public int getExpReward() {
				return 1000; 
			}

			@Override
			public int getLevelNeed() {
				return 35;
			}

			@Override
			public DonjonDifficulty getDonjonDifficulty() {
				return DonjonDifficulty.DIFFICILE;
			}
		});
		return list;
	}

}
