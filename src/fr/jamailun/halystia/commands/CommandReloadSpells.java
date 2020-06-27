package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandReloadSpells extends HalystiaCommand {

	public CommandReloadSpells(HalystiaRPG main) {
		super(main, "reload-spells");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		main.getSpellLoader().reloadSpells();
		sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Les sorts ont été reload !");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
}