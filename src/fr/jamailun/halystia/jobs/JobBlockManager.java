package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.List;

public class JobBlockManager implements JobContent<JobBlock> {

	private List<JobBlock> blocs;

	public JobBlockManager() {
		blocs = new ArrayList<>();
	}
	
	@Override
	public void registerContent(JobBlock bloc) {
		if( ! blocs.contains(bloc) )
			blocs.add(bloc);
	}

	@Override
	public void unregisterCremoveContent(JobBlock bloc) {
		if( blocs.contains(bloc) )
			blocs.remove(bloc);
	}

	@Override
	public List<JobBlock> getRegisteredContent() {
		return new ArrayList<>(blocs);
	}

}
