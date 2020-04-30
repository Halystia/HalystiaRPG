package fr.jamailun.halystia.jobs2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobBlockManager implements JobContent<JobBlock> {

	private Set<JobBlock> blocs;

	public JobBlockManager() {
		blocs = new HashSet<>();
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
