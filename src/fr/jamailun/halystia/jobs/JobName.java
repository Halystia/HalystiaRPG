package fr.jamailun.halystia.jobs;

import org.bukkit.Material;

public enum JobName {
	
	EMPTY("empty", JobSlot.VOID),

	FORGERON("Forgeron", JobSlot.CRAFT),
	
	PECHEUR("Pêcheur", JobSlot.RECOLTE),
	AGRICULTEUR("Agriculteur", JobSlot.RECOLTE),
	MINEUR("Mineur", JobSlot.RECOLTE),
	BUCHERON("Bûcheron", JobSlot.RECOLTE);
	
	public final String stringName;
	public final JobSlot type;
	
	private JobName(String str, JobSlot type) {
		stringName = str;
		this.type = type;
	}
	
	public JobSlot getJobSlot() {
		return type;
	}
	
	public static JobName fromString(String str) {
		for(JobName j : values())
			if(j.stringName.equalsIgnoreCase(str))
				return j;
		return null;
	}
	
	public String getName() {
		return stringName;
	}
	
	public String getConfigName() {
		return toString().toLowerCase();
	}

	public Material getIcon(int level) {
		switch(this) {
			case AGRICULTEUR:
				if(level <= 3)
					return Material.WOODEN_HOE;
				if(level <= 7)
					return Material.STONE_HOE;
				if(level <= 9)
					return Material.GOLDEN_HOE;
				if(level <= 12)
					return Material.IRON_HOE;
				if(level <= 15)
					return Material.DIAMOND_HOE;
			case MINEUR:
				if(level <= 3)
					return Material.WOODEN_PICKAXE;
				if(level <= 7)
					return Material.STONE_PICKAXE;
				if(level <= 9)
					return Material.GOLDEN_PICKAXE;
				if(level <= 12)
					return Material.IRON_PICKAXE;
				if(level <= 15)
					return Material.DIAMOND_PICKAXE;
			case BUCHERON:
				if(level <= 3)
					return Material.WOODEN_AXE;
				if(level <= 7)
					return Material.STONE_AXE;
				if(level <= 9)
					return Material.GOLDEN_AXE;
				if(level <= 12)
					return Material.IRON_AXE;
				if(level <= 15)
					return Material.DIAMOND_AXE;
			default:
				return Material.BARRIER;
		}
	}
}