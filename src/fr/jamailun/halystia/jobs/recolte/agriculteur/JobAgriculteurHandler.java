package fr.jamailun.halystia.jobs.recolte.agriculteur;

import fr.jamailun.halystia.jobs.JobHandler;
import fr.jamailun.halystia.jobs.JobName;

public class JobAgriculteurHandler extends JobHandler {
	public JobAgriculteurHandler(String pathFolder) {
		super(pathFolder, JobName.AGRICULTEUR, new JobAgriculteurData());
	}
}