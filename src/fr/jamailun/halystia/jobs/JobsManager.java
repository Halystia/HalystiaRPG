package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobResult.Type;

public final class JobsManager {
	
	private List<JobType> jobs;
	private final JobBlockManager blocs;
	private final JobCraftsManager crafts;
	private final JobsItemManager items;
	
	public JobsManager() {
		jobs = new ArrayList<>();
		this.blocs = new JobBlockManager();
		this.crafts = new JobCraftsManager();
		this.items = new JobsItemManager();
	}

	public JobCraftsManager getCraftsManager() {
		return crafts;
	}

	public JobBlockManager getBlocsManager() {
		return blocs;
	}
	
	public JobsItemManager getItemManager() {
		return items;
	}
	
	public void registerJob(JobType job) {
		if ( ! jobs.contains(job) )
			jobs.add(job);
	}
	
	public void unregisterJob(JobType job) {
		jobs.remove(job);
	}
	
	public boolean isRegisteredBlock(Material type) {
		for(JobBlock block : blocs.getRegisteredContent())
			if(block.getType() == type)
				return true;
		return false;
	}
	
	public List<JobType> getJobsOfPlayer(Player p) {
		return jobs.stream().filter(j -> j.hasJob(p)).collect(Collectors.toList());
	}

	public void saveJobs() {
		jobs.forEach(j -> j.saveData());
	}

	public synchronized boolean isCraftBlock(Material type, Player p) {
		for ( JobType job : jobs ) {
			if ( job.getCraftBlock() == type ) {
				if( ! job.hasJob(p)) {
					p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous n'avez pas le bon métier. Il faut être " + ChatColor.GOLD + job.getJobName() + ChatColor.RED + ".");
					return false;
				}
				job.getCraftGUI().openGUItoPlayer(p);
				return true;
			}
		}
		return false;
	}
	
	public JobResult blockBreakEvent(Block b, Player p) {
		for(JobBlock bloc : blocs.getRegisteredContent()) {
			if(bloc.getType() != b.getType())
				continue;
			JobType job = bloc.getJob();
			if( ! job.hasJob(p))
				return new JobResult(Type.NO_JOB);
			if(bloc.getLevel() > job.getPlayerLevel(p))
				return new JobResult(Type.NO_LEVEL);
			job.addExp(bloc.getXp(), p);
			return new JobResult(Type.SUCCESS, bloc);
		}
		return new JobResult(Type.NO_BLOCK);
	}
	
	public JobType getJobWithString(String name) {
		for(JobType job : jobs)
			if(job.getJobName().equals(name))
				return job;
		return null;
	}
	
	public List<String> getAllJobTypesNames() {
		return jobs.stream().map(j -> j.getJobName()).collect(Collectors.toList());
	}
	
}