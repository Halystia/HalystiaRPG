package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.custom.enchantment.EnchantementType;
import fr.jamailun.halystia.custom.enchantment.EnchantmentReader;

public class CommandCustomEnchant extends HalystiaCommand {
	
	public CommandCustomEnchant(HalystiaRPG main) {
		super(main, "enchant-item");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être un joueur.");
			return true;
		}
		Player p = (Player) sender;
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut avoir un item en main.");
			return true;
		}
		
		if(args.length < 1) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "/"+arg1+" <enchant> [level]");
			return true;
		}
		
		EnchantementType type = EnchantementType.getFromString(args[0]);
		if(type == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Enchantement non reconnu. Liste des enchantements :");
			p.sendMessage(EnchantementType.getStrings().toArray().toString());
			return true;
		}
		
		int level = 1;
		if(args.length <= 2) {
			try {
				level = Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Nombre non reconnu !");
				return true;
			}
		}
		
		if(level < 0) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le niveau doit être positif ou nul.");
			return true;
		}
		
		if(level > 20) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le niveau ne doit pas dépasser 20.");
			return true;
		}
		
		if(EnchantmentReader.enchantItem(item, type, level))
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Enchantement effectué avec succès.");
		else
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Enchantement échoué.");
			
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return EnchantementType.getStrings().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}

}