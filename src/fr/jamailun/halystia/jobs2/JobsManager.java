package fr.jamailun.halystia.jobs2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.jobs2.JobResult.Type;

public final class JobsManager {
	
	private Set<JobType> jobs;
	private final JobBlockManager blocs;
	private final JobCraftsManager crafts;
	
	public JobsManager() {
		this.blocs = new JobBlockManager();
		this.crafts = new JobCraftsManager();
	}

	public JobCraftsManager getCraftsManager() {
		return crafts;
	}

	public JobBlockManager getBlocsManager() {
		return blocs;
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

	public boolean isCraftBlock(Material type, Player p) {
		for ( JobType job : jobs ) {
			if ( job.getCraftBlock() == type ) {
				job.openJobInventory(p);
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