package fr.jamailun.halystia.jobs.recolte.agriculteur;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.jobs.JobName;
import fr.jamailun.halystia.jobs.recolte.JobRecolte;
import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;
import fr.jamailun.halystia.jobs.system.HeadCreator;
import fr.jamailun.halystia.utils.ItemBuilder;

public class JobAgriculteurData extends JobRecolte {

	public final static ItemBuilder FRAISE = new ItemBuilder(HeadCreator.createHead("1acb2610-4fc4-46b0-8dae-679ccb31f057", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));
	public final static ItemBuilder CITRON = new ItemBuilder(HeadCreator.createHead("ac0dae67-1332-9a6e-15b7-63201dd4e885", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3OGI1ODJkMTljY2M1NWIwMjNlYjgyZWRhMjcxYmFjNDc0NGZhMjAwNmNmNWUxOTAyNDZlMmI0ZDVkIn19fQ=="));
	public final static ItemBuilder PWOARE = new ItemBuilder(HeadCreator.createHead("8a1a7bb7-6022-4efe-b529-7e923f186449", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgzMzcwZTA3MTc0YWJiN2M3YWYxOGM0ZWNkN2RhMWQ4MTNhYTM5NGYxNjEwODU0Mjk3YmQ1ZGU3ZDJmNDg5In19fQ=="));
	public final static ItemBuilder RAISIN = new ItemBuilder(HeadCreator.createHead("6a91273a-275f-4509-b7aa-f1928302867c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDUxMWE1ZWU0ZDE3NjgyYTI1ZjdlOGE1ZGE2ZmY3Y2Q5YWQ5YzQ4NDRjMjU4YTZkZTIzZTdmODRmMjdmOWI0In19fQ=="));
	public final static ItemBuilder FRAMBO = new ItemBuilder(HeadCreator.createHead("1508fef4-f03b-483c-9c61-94efbeae203c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEyZWYxYjQ4NmU5N2U0Y2IxMjRhYTc2MjlhY2ViOTFlZGM1MWQ2MzMzOGM5MWEwMTI4ODU0OTNjNWQ5YyJ9fX0="));
	
	public final static ItemBuilder MONSTR = new ItemBuilder(PotionEffectType.UNLUCK, 0, 0, Color.PURPLE, false, 1).addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
	
	public JobAgriculteurData() {
		addData(new JobRecolteBloc(Material.WHEAT, Material.WOODEN_HOE, 1, new ItemBuilder(Material.WHEAT).setName(c()+"Blé doré")));
		addData(new JobRecolteBloc(Material.CARROTS, Material.WOODEN_HOE, 1, new ItemBuilder(Material.CARROT).setName(c()+"Cawotte")));
		
		addData(new JobRecolteBloc(Material.POTATOES, Material.WOODEN_HOE, 2, new ItemBuilder(Material.POTATO).setName(c()+"Pagoï")));
		addData(new JobRecolteBloc(Material.VINE, Material.WOODEN_HOE, 2, new ItemBuilder(Material.VINE).setName(c()+"Herbes aromatiques")));
		
		addData(new JobRecolteBloc(Material.BEETROOTS, Material.WOODEN_HOE, 3, new ItemBuilder(Material.BEETROOT_SOUP).setName(c()+"Sauce rouge (végétale)")));
		addData(new JobRecolteBloc(Material.GRASS, Material.WOODEN_HOE, 3, new ItemBuilder(Material.WHEAT_SEEDS).setName(c()+"Graines")));

		addData(new JobRecolteBloc(Material.WHEAT, Material.STONE_HOE, 4, new ItemBuilder(Material.WHEAT).setName(c()+"Orge")));

		addData(new JobRecolteBloc(Material.ACACIA_LEAVES, Material.STONE_HOE, 5, FRAISE.setName(c()+"Fraise")));
		addData(new JobRecolteBloc(Material.SPRUCE_LEAVES, Material.STONE_HOE, 5, RAISIN.setName(c()+"Raisin")));
		
		addData(new JobRecolteBloc(Material.SUGAR_CANE, Material.STONE_HOE, 6, new ItemBuilder(Material.SUGAR).setName(c()+"Sucre")));

		addData(new JobRecolteBloc(Material.OAK_LEAVES, Material.STONE_HOE, 7, new ItemBuilder(Material.APPLE).setName(c()+"Pom")));
		addData(new JobRecolteBloc(Material.BIRCH_LEAVES, Material.STONE_HOE, 7, FRAMBO.setName(c()+"Framboose")));

		addData(new JobRecolteBloc(Material.WHEAT, Material.GOLDEN_HOE, 8, new ItemBuilder(Material.APPLE).setName(c()+"Avoine")));
		addData(new JobRecolteBloc(Material.BEETROOTS, Material.GOLDEN_HOE, 8, new ItemBuilder(Material.BEETROOT).setName(c()+"Betterave")));

		addData(new JobRecolteBloc(Material.CARROTS, Material.GOLDEN_HOE, 9, new ItemBuilder(Material.GOLDEN_CARROT).setName(c()+"Carotte dorée")));
		
		addData(new JobRecolteBloc(Material.WHEAT, Material.IRON_HOE, 10, new ItemBuilder(Material.WHEAT).setName(c()+"Millet")));
		addData(new JobRecolteBloc(Material.MYCELIUM, Material.IRON_HOE, 10, new ItemBuilder(Material.BROWN_MUSHROOM).setName(c()+"Truffe")));

		addData(new JobRecolteBloc(Material.ACACIA_LEAVES, Material.IRON_HOE, 11, PWOARE.setName(c()+"Poare")));
		addData(new JobRecolteBloc(Material.BIRCH_LEAVES, Material.IRON_HOE, 11, CITRON.setName(c()+"Zitron")));

		addData(new JobRecolteBloc(Material.WHITE_CONCRETE_POWDER, Material.IRON_HOE, 12, new ItemBuilder(Material.SUGAR).setName(c()+"Sel de mer")));
		addData(new JobRecolteBloc(Material.BLACK_CONCRETE_POWDER, Material.IRON_HOE, 12, new ItemBuilder(Material.GUNPOWDER).setName(c()+"Poivre")));

		addData(new JobRecolteBloc(Material.SPONGE, Material.DIAMOND_HOE, 13, new ItemBuilder(Material.SPONGE).setName(c()+"Bout de fromage")));
		
		addData(new JobRecolteBloc(Material.PUMPKIN, Material.DIAMOND_HOE, 14, new ItemBuilder(Material.PUMPKIN).setName(c()+"Passtek")));
		addData(new JobRecolteBloc(Material.MELON, Material.DIAMOND_HOE, 14, new ItemBuilder(Material.MELON).setName(c()+"Pitwouille")));

		addData(new JobRecolteBloc(Material.SOUL_SAND, Material.DIAMOND_HOE, 15, MONSTR.setName(c()+"Essence monstrueuse")));
	}

	@Override
	public JobName getJobName() {
		return JobName.AGRICULTEUR;
	}

}