package fr.jamailun.halystia.jobs.recolte.bucheron;

import fr.jamailun.halystia.jobs.JobHandler;
import fr.jamailun.halystia.jobs.JobName;

public class JobBucheronHandler extends JobHandler {
	public JobBucheronHandler(String pathFolder) {
		super(pathFolder, JobName.BUCHERON, new JobBucheronData());
	}
}