package fr.jamailun.halystia.jobs2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JobCraftsManager implements JobContent<JobCraft> {
	
	private Set<JobCraft> crafts;

	public JobCraftsManager() {
		crafts = new HashSet<>();
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