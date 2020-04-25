package fr.jamailun.halystia.jobs.recolte.mineur;

import org.bukkit.Material;

import fr.jamailun.halystia.jobs.JobName;
import fr.jamailun.halystia.jobs.recolte.JobRecolte;
import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;
import fr.jamailun.halystia.utils.ItemBuilder;

public class JobMineurData extends JobRecolte {

	public JobMineurData() {
		addData(new JobRecolteBloc(Material.COBBLESTONE, Material.WOODEN_PICKAXE, 1, new ItemBuilder(Material.COBBLESTONE).setName(c()+"Cailloux")));
	}

	@Override
	public JobName getJobName() {
		return JobName.MINEUR;
	}

}