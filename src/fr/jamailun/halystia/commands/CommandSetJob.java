package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobType;
import fr.jamailun.halystia.jobs.JobsManager;

public class CommandSetJob extends HalystiaCommand {

	public static final List<String> list = Arrays.asList("add", "remove", "list", "xp");
	
	private final JobsManager jobs;
	
	public CommandSetJob(HalystiaRPG main, JobsManager jobs) {
		super(main, "set-job");
		this.jobs = jobs;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( (sender instanceof Player)) {
			if(! HalystiaRPG.isInRpgWorld(((Player)sender))) {
				sender.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
				return true;
			}
		}
		
		if(args.length < 2) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "/"+arg1+" <joueur> <commande>");
			return true;
		}
		
		Player cible = Bukkit.getPlayer(args[0]);
		if(cible == null) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "Le joueur ["+args[0]+"] n'existe pas ou n'est pas connecté.");
			return true;
		}
		
		if(args[1].equals("list")) {
			List<JobType> jobA = jobs.getJobsOfPlayer(cible);
			if(jobA.isEmpty()) {
				sender.sendMessage(BLUE + "Le joueur " + cible.getName()+ " n'a aucun métier.");
				return true;
			}
			sender.sendMessage(BLUE + "Métiers de " + cible.getName() + " :");
			for(JobType job : jobA)
				if(job != null)
					sender.sendMessage(BLUE + "-" + job.getJobName() + " : "+job.getPlayerLevel(cible)+" ("+job.getPlayerExp(cible) + ")");
			sender.sendMessage(BLUE + "----------------------------");
			return true;
		}
		
		if(args.length < 3) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "/"+arg1+" "+args[0]+" " + args[1] + "[job]");
			return true;
		}
		
		JobType job = jobs.getJobWithString(args[2]);
		if(job == null) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "Le métier ["+args[2]+"] n'existe pas.");
			return true;
		}
		
		if(args[1].equals("add")) {
			if(jobs.getJobsOfPlayer(cible).size() >= 2) {
				sender.sendMessage(RED + "Impossible : ce joueur a trop de métiers.");
				return true;
			}
			if(job.hasJob(cible)) {
				sender.sendMessage(RED + "Impossible : ce joueur a déjà ce métier.");
				return true;
			}
			job.registerPlayer(cible);
			sender.sendMessage(GREEN + "Succès de la requête.");
			return true;
		}
		
		if(args[1].equals("remove")) {
			if( ! job.hasJob(cible)) {
				sender.sendMessage(RED + "Impossible : ce joueur n'a pas ce métier.");
				return true;
			}
			job.unregisterPlayer(cible);
			sender.sendMessage(GREEN + "Succès de la requête.");
			return true;
		}
		
		if(args[1].equals("xp")) {
			if( ! job.hasJob(cible)) {
				sender.sendMessage(RED + "Ce joueur n'a pas ce métier.");
				return true;
			}
			if(args.length == 3) {
				int exp = job.getPlayerExp(cible);
				sender.sendMessage(GREEN + "Joueur " + GOLD + cible.getName() + GREEN + " - " + BLUE + job.toString() + GREEN + " : Niveau " + YELLOW + exp + GREEN + "xp, level " + GOLD + job.getLevel(exp)+GREEN+".");
				return true;
			}
			int xp = -1;
			try {
				xp = Integer.parseInt(args[3]);
				if(xp < 0)
					xp = 0;
				final int expMax = job.getXpRequired(5);
				if ( xp > expMax )
					xp = expMax;
			} catch (NumberFormatException e) {
				sender.sendMessage(RED + "Format du nombre invalide.");
				return true;
			};
			job.forceExp(cible, xp);
			sender.sendMessage(GREEN + "Exp validé ! Le joueur est désormais niveau " + GOLD + job.getLevel(xp) + GREEN + ".");
			cible.sendMessage(RED + "Attention ! " + GRAY + "Un administrateur a fixé ton exp de "+GOLD + job.getJobName()+GRAY + " à " + GOLD + xp+"xp"+GRAY+". Vous êtes désormais niveau " + GOLD + job.getLevel(xp) + GRAY +".");
			return true;
		}
		
		sender.sendMessage(RED + "Commande inconnue ! (add,remove,list,xp).");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return Bukkit.getOnlinePlayers().stream().filter(p -> HalystiaRPG.isInRpgWorld(p) && p.getName().startsWith(args[0])).map(p -> p.getName()).collect(Collectors.toList());
		if(args.length <= 2)
			return list.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
		if(args.length <= 3 && ( ! args[1].equals("list"))) {
			Player cible = Bukkit.getPlayer(args[0]);
			if(cible == null)
				return new ArrayList<>();
			if(args[1].equals("remove") || args[1].equals("xp"))
				return jobs.getJobsOfPlayer(cible).stream().map(j -> j.getJobName().toString()).filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
			if(args[1].equals("add"))
				return jobs.getAllJobTypesNames().stream().filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
}