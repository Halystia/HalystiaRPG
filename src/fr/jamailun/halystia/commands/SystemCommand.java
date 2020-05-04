package fr.jamailun.halystia.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobType;

public class SystemCommand extends HalystiaCommand {

	public SystemCommand(HalystiaRPG main) {
		super(main, "systemctl");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player))
			return true;
		if(args.length < 2)
			return true;
		Player p = (Player) sender;
		if(args[0].equals("learn__job")) {
			if( p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() != Material.WRITTEN_BOOK )
				return true;
			JobType job = main.getJobManager().getJobWithString(args[1]);
			if(job == null)
				return true;
			if(main.getJobManager().getJobsOfPlayer(p).size() >= 2) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Tu as trop de métiers !");
				return true;
			}
			if(job.hasJob(p)) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Tu as déjà ce métier !");
				return true;
			}
			p.getInventory().getItemInMainHand().setAmount(0);
			job.registerPlayer(p);
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vous avez réussi à apprendre le métier de " +ChatColor.GOLD+ job.getJobName()+ChatColor.GREEN+".");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return null;
	}

}