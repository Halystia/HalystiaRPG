package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobResult.Type;
import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;
import fr.jamailun.halystia.jobs.recolte.agriculteur.JobAgriculteurHandler;
import fr.jamailun.halystia.jobs.recolte.bucheron.JobBucheronHandler;
import fr.jamailun.halystia.jobs.recolte.mineur.JobMineurHandler;
import fr.jamailun.halystia.utils.ItemBuilder;

public class JobManager {
	
	public final static int SAVE_FREQ = 180;
	public static final int HOW_MANY_JOBS = 2;
	
	private Set<JobHandler> jobs;
	
	public JobManager(String folder, HalystiaRPG main) {
		jobs = new HashSet<>();
		
		jobs.add(new JobAgriculteurHandler(folder));
		jobs.add(new JobMineurHandler(folder));
		jobs.add(new JobBucheronHandler(folder));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				saveJobs();
			}
		}.runTaskTimer(main, SAVE_FREQ*20L, SAVE_FREQ*20L);
	}
	
	public void saveJobs() {
		jobs.forEach(j -> j.saveData());
	}
	
	/**
	 * To call when a bloc is broke.
	 */
	public JobResult blockBreakEvent(Block b, Player p, Material tool) {
		for(JobHandler job : jobs) {
			JobRecolteBloc blocData = job.getBlockDataFromBlock(b, tool);
			if(blocData == null)
				continue;
			//on a trouvé le job !
			if( ! job.hasJob(p))
				return new JobResult(Type.NO_JOB);
			if(blocData.getLevelRequired() > job.getPlayerLevel(p))
				return new JobResult(Type.NO_LEVEL);
			if(tool != blocData.getToolType())
				return new JobResult(Type.NO_TOOL);
			
			job.addExp(blocData.getExpGiven(), p);
			return new JobResult(Type.SUCCESS, blocData);
		}
		return new JobResult(Type.NOT_BLOCK);
	}
	
	public boolean registerNewJob(JobHandler job) {
		return jobs.add(job);
	}
	
	public boolean hasJob(Player p, JobName job) {
		for(JobHandler handler : jobs)
			if(handler.getJobName() == job)
				if(handler.hasJob(p))
					return true;
				else
					return false;
		return false;
	}
	
	public JobHandler[] getJobs(Player p) {
		List<JobHandler> list = new ArrayList<>();
		
		for(JobHandler job : jobs)
			if(job != null)
				if(job.hasJob(p))
					list.add(job);
		
		JobHandler[] array = new JobHandler[list.size()];
		int j = 0;
		for(JobHandler job : list) {
			array[j] = job;
			j++;
		}
		return array;
	}
	
	public boolean addJob(Player p, JobName name) {
		if(name == JobName.EMPTY)
			return false;
		for(JobHandler handler : getJobs(p))
			if(handler != null)
				if(handler.getJobName() == name)
					return false;
		for(JobHandler job : jobs) {
			if(job.getJobName() != name)
				continue;
			// Success
			job.registerPlayer(p);
			return true;
		}
		Bukkit.getLogger().log(Level.SEVERE, "ALERT : job ("+name+") not defined.");
		return false;
	}
	
	public boolean removeJob(Player p, JobName name) {
		JobHandler[] array = getJobs(p);
		for ( int i = 0 ; i < HOW_MANY_JOBS ; i++ ) {
			if ( array[i].getJobName() == name ) {
				array[i].unregisterPlayer(p);
				return true;
			}
		}
		return false;
	}

	public List<ItemStack> getIcons(Player p, JobName job) {
		for(JobHandler handler : jobs)
			if(handler.getJobName() == job)
				return handler.getIcons(handler.getPlayerLevel(p));
		return Arrays.asList(new ItemBuilder(Material.REDSTONE_BLOCK).setName(ChatColor.RED+"Erreur.").toItemStack());
	}
}