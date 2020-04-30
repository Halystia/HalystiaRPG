package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobCraftsManager implements JobContent<JobCraft> {
	
	private List<JobCraft> crafts;

	public JobCraftsManager() {
		crafts = new ArrayList<>();
	}
	
	@Override
	public void registerContent(JobCraft craft) {
		if( ! crafts.contains(craft) )
			crafts.add(craft);
	}

	@Override
	public void unregisterCremoveContent(JobCraft craft) {
		if( crafts.contains(craft) )
			crafts.remove(craft);
	}

	public List<JobCraft> getCrafts(JobType jobType) {
		return crafts.stream().filter(craft -> craft.getJob().equals(jobType)).collect(Collectors.toList());
	}

	@Override
	public List<JobCraft> getRegisteredContent() {
		return new ArrayList<>(crafts);
	}
}