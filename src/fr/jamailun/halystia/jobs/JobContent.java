package fr.jamailun.halystia.jobs;

import java.util.List;

public interface JobContent<T> {

	public void registerContent(T content);
	
	public void unregisterCremoveContent(T content);
	
	public List<T> getRegisteredContent();
}