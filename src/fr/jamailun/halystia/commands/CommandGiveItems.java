package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobsManager;

public class CommandGiveItems extends HalystiaCommand {

	private final JobsManager jobs;
	public CommandGiveItems(HalystiaRPG main, JobsManager jobs) {
		super(main, "give-item");
		this.jobs = jobs;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		if(args.length < 1) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut spécifier l'item à recevoir !");
			return true;
		}
		
		Player p = (Player) sender;
		ItemStack item = jobs.getItemManager().getWithKey(args[0]);
		if(item == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Item '"+args[0]+"' non reconnu.");
			return true;
		}
		p.getInventory().addItem(item);
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Succès de la requête.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return jobs.getItemManager().getAllKeys().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}

}
