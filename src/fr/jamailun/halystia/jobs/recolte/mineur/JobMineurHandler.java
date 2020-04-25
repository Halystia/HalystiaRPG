package fr.jamailun.halystia.jobs.recolte.mineur;

import fr.jamailun.halystia.jobs.JobHandler;
import fr.jamailun.halystia.jobs.JobName;

public class JobMineurHandler extends JobHandler {
	public JobMineurHandler(String path) {
		super(path, JobName.MINEUR, new JobMineurData());
	}
}