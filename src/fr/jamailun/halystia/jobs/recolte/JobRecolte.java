package fr.jamailun.halystia.jobs.recolte;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.jamailun.halystia.jobs.JobData;
import fr.jamailun.halystia.jobs.JobSlot;

public abstract class JobRecolte extends JobData {

	private Set<JobRecolteBloc> blocs;
	
	public JobRecolte() {
		blocs = new HashSet<>();
	}
	
	@Override
	public final JobSlot getSlot() {
		return JobSlot.RECOLTE;
	}
	
	/**
	 * Get assossiated block data.
	 * @param block : {@link org.bukkit.block.Block Block} to test.
	 * @param tool 
	 * @return null if no block matches.
	 */
	public JobRecolteBloc getBlocData(Block block, Material tool) {
		for(JobRecolteBloc rb : blocs)
			if(rb.matchesData(block, tool))
				return rb;
		return null;
	}
	
	protected void addData(JobRecolteBloc data) {
		blocs.add(data);
	}
	
	protected void addAllData(Collection<JobRecolteBloc> data) {
		blocs.addAll(data);
	}
	
	protected final String c() {
		return ChatColor.YELLOW + "" + ChatColor.BOLD;
	}
	
	public final Set<JobRecolteBloc> getData() {
		return new HashSet<>(blocs);
	}
	
}