package fr.jamailun.halystia.jobs.recolte.bucheron;

import org.bukkit.Material;

import fr.jamailun.halystia.jobs.JobName;
import fr.jamailun.halystia.jobs.recolte.JobRecolte;
import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;
import fr.jamailun.halystia.jobs.system.HeadCreator;
import fr.jamailun.halystia.utils.ItemBuilder;

public class JobBucheronData extends JobRecolte {

	private final static ItemBuilder MURE = new ItemBuilder(HeadCreator.createHead("73ebeb97-286c-479f-91ff-7b178da675e8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
	
	public JobBucheronData() {
		
		addData(new JobRecolteBloc(Material.OAK_LOG, Material.WOODEN_AXE, 1, new ItemBuilder(Material.OAK_LOG).setName(c()+"Bûche de zaraï")));	
		addData(new JobRecolteBloc(Material.SPRUCE_LOG, Material.WOODEN_AXE, 1, new ItemBuilder(Material.SPRUCE_LOG).setName(c()+"Bûche de solonia")));
		
		addData(new JobRecolteBloc(Material.BIRCH_LOG, Material.WOODEN_AXE, 2, new ItemBuilder(Material.BIRCH_LOG).setName(c()+"Bûche de wirrd")));	
		addData(new JobRecolteBloc(Material.ACACIA_LOG, Material.WOODEN_AXE, 2, new ItemBuilder(Material.ACACIA_LOG).setName(c()+"Bûche de tolonn")));
		
		addData(new JobRecolteBloc(Material.DARK_OAK_LOG, Material.WOODEN_AXE, 3, new ItemBuilder(Material.DARK_OAK_LOG).setName(c()+"Bûche d'irmen")));
		addData(new JobRecolteBloc(Material.JUNGLE_LOG, Material.WOODEN_AXE, 3, new ItemBuilder(Material.JUNGLE_LOG).setName(c()+"Bûche de trinu")));

		addData(new JobRecolteBloc(Material.OAK_LEAVES, Material.STONE_AXE, 4, new ItemBuilder(Material.STICK).setName(c()+"Baton")));

		addData(new JobRecolteBloc(Material.HONEYCOMB_BLOCK, Material.STONE_AXE, 5, new ItemBuilder(Material.HONEY_BOTTLE).setName(c()+"Fiole de miel")));
		addData(new JobRecolteBloc(Material.OAK_LEAVES, Material.STONE_AXE, 5, MURE.setName(c()+"Mûre")));

		addData(new JobRecolteBloc(Material.FIRE_CORAL_BLOCK, Material.STONE_AXE, 6, new ItemBuilder(Material.FIRE_CORAL_BLOCK).setName(c()+"Bloc de corail de feu")));
		addData(new JobRecolteBloc(Material.TUBE_CORAL_BLOCK, Material.STONE_AXE, 6, new ItemBuilder(Material.TUBE_CORAL_BLOCK).setName(c()+"Bloc de corail tubulaire")));
		addData(new JobRecolteBloc(Material.HORN_CORAL_BLOCK, Material.STONE_AXE, 6, new ItemBuilder(Material.HORN_CORAL_BLOCK).setName(c()+"Bloc de corail corné")));
		addData(new JobRecolteBloc(Material.BUBBLE_CORAL_BLOCK, Material.STONE_AXE, 6, new ItemBuilder(Material.BUBBLE_CORAL_BLOCK).setName(c()+"Bloc de corail bulles")));
		addData(new JobRecolteBloc(Material.BRAIN_CORAL_BLOCK, Material.STONE_AXE, 6, new ItemBuilder(Material.BRAIN_CORAL_BLOCK).setName(c()+"Bloc de corail cerveau")));
		
		addData(new JobRecolteBloc(Material.POPPY, Material.STONE_AXE, 7, new ItemBuilder(Material.POPPY).setName(c()+"Coquelicot des prés")));
		addData(new JobRecolteBloc(Material.DANDELION, Material.STONE_AXE, 7, new ItemBuilder(Material.DANDELION).setName(c()+"Fleur à crinière")));
		addData(new JobRecolteBloc(Material.ORANGE_TULIP, Material.STONE_AXE, 7, new ItemBuilder(Material.ORANGE_TULIP).setName(c()+"Tulipe orange")));
		addData(new JobRecolteBloc(Material.RED_TULIP, Material.STONE_AXE, 7, new ItemBuilder(Material.RED_TULIP).setName(c()+"Tulipe rouge")));
		addData(new JobRecolteBloc(Material.WHITE_TULIP, Material.STONE_AXE, 7, new ItemBuilder(Material.WHITE_TULIP).setName(c()+"Tulipe blanche")));
		addData(new JobRecolteBloc(Material.PINK_TULIP, Material.STONE_AXE, 7, new ItemBuilder(Material.PINK_TULIP).setName(c()+"Tulipe rose")));
		
		addData(new JobRecolteBloc(Material.BROWN_MUSHROOM, Material.GOLDEN_AXE, 8, new ItemBuilder(Material.BROWN_MUSHROOM).setName(c()+"Cèpe")));
		addData(new JobRecolteBloc(Material.RED_MUSHROOM, Material.GOLDEN_AXE, 8, new ItemBuilder(Material.RED_MUSHROOM).setName(c()+"Amanite")));

		addData(new JobRecolteBloc(Material.ROSE_BUSH, Material.GOLDEN_AXE, 9, new ItemBuilder(Material.ROSE_BUSH).setName(c()+"Rosier des forêts")));
		addData(new JobRecolteBloc(Material.LILAC, Material.GOLDEN_AXE, 9, new ItemBuilder(Material.LILAC).setName(c()+"Lila des plaines")));
		addData(new JobRecolteBloc(Material.PEONY, Material.GOLDEN_AXE, 9, new ItemBuilder(Material.PEONY).setName(c()+"Pivoine des landes")));
		
		addData(new JobRecolteBloc(Material.BEE_NEST, Material.GOLDEN_AXE, 10, new ItemBuilder(Material.BEE_NEST).setName(c()+"Nid d'abeille")));
		addData(new JobRecolteBloc(Material.BEEHIVE, Material.GOLDEN_AXE, 10, new ItemBuilder(Material.BEEHIVE).setName(c()+"Ruche")));
		
		addData(new JobRecolteBloc(Material.VINE, Material.IRON_AXE, 11, new ItemBuilder(Material.VINE).setName(c()+"Liane")));

		addData(new JobRecolteBloc(Material.WITHER_ROSE, Material.IRON_AXE, 12, new ItemBuilder(Material.GUNPOWDER).setName(c()+"Sciure noire")));
		addData(new JobRecolteBloc(Material.COMPOSTER, Material.IRON_AXE, 12, new ItemBuilder(Material.BONE_MEAL, 5).setName(c()+"Engrais")));

		addData(new JobRecolteBloc(Material.OAK_LOG, Material.DIAMOND_AXE, 13, new ItemBuilder(Material.OAK_LOG).setName(c()+"Bois de kokou")));
		addData(new JobRecolteBloc(Material.SPRUCE_LOG, Material.DIAMOND_AXE, 13, new ItemBuilder(Material.SPRUCE_LOG).setName(c()+"Bois de kiresta")));
		
		addData(new JobRecolteBloc(Material.RED_MUSHROOM_BLOCK, Material.DIAMOND_AXE, 14, new ItemBuilder(Material.RED_MUSHROOM_BLOCK, 3).setName(c()+"Bloc de champignon rouge")));
		
		addData(new JobRecolteBloc(Material.SOUL_SAND, Material.DIAMOND_AXE, 15, new ItemBuilder(Material.DARK_OAK_LOG).shine().setName(c()+"Bois démoniaque")));
		
	}
	
	@Override
	public JobName getJobName() {
		return JobName.BUCHERON;
	}

}