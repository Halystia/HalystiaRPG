package fr.jamailun.halystia.jobs;

import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;

public final class JobResult {
	
	private final Type type;
	private final JobRecolteBloc blocData;
	
	public JobResult(Type type) {
		this(type, null);
	}
	
	public JobResult(Type type, JobRecolteBloc blocData) {
		this.type = type;
		this.blocData = blocData;
	}
	
	public Type getResultType() {
		return type;
	}
	
	public JobRecolteBloc getData() {
		return blocData;
	}
	
	public enum Type {
		NO_LEVEL, NO_TOOL, NOT_BLOCK, NO_JOB, SUCCESS
	}
}