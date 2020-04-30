package fr.jamailun.halystia.jobs2;

public final class JobResult {

	private final Type type;
	private final JobBlock data;
	
	public JobResult(Type type) {
		this(type, null);
	}
	
	public JobResult(Type type, JobBlock data) {
		this.type = type;
		this.data = data;
	}
	
	public JobBlock getData() {
		return data;
	}

	public Type getType() {
		return type;
	}

	public enum Type {
		NO_LEVEL, NO_JOB, NO_BLOCK, SUCCESS
	}
}