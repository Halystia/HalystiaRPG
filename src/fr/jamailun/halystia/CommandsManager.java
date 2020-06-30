package fr.jamailun.halystia;

import fr.jamailun.halystia.commands.CommandClasse;
import fr.jamailun.halystia.commands.CommandCreateShop;
import fr.jamailun.halystia.commands.CommandCustomEnchant;
import fr.jamailun.halystia.commands.CommandEditChunks;
import fr.jamailun.halystia.commands.CommandEditDonjons;
import fr.jamailun.halystia.commands.CommandEditEquipment;
import fr.jamailun.halystia.commands.CommandEditMobs;
import fr.jamailun.halystia.commands.CommandEditNPC;
import fr.jamailun.halystia.commands.CommandEditQuests;
import fr.jamailun.halystia.commands.CommandEditTitles;
import fr.jamailun.halystia.commands.CommandGiveCanne;
import fr.jamailun.halystia.commands.CommandGiveItems;
import fr.jamailun.halystia.commands.CommandGivePotion;
import fr.jamailun.halystia.commands.CommandGiveSpell;
import fr.jamailun.halystia.commands.CommandGiveWater;
import fr.jamailun.halystia.commands.CommandNpcTeleport;
import fr.jamailun.halystia.commands.CommandPing;
import fr.jamailun.halystia.commands.CommandPurge;
import fr.jamailun.halystia.commands.CommandQuests;
import fr.jamailun.halystia.commands.CommandReloadShops;
import fr.jamailun.halystia.commands.CommandReloadSpells;
import fr.jamailun.halystia.commands.CommandSetChunk;
import fr.jamailun.halystia.commands.CommandSetJob;
import fr.jamailun.halystia.commands.CommandSetKarma;
import fr.jamailun.halystia.commands.CommandSetRoi;
import fr.jamailun.halystia.commands.CommandSetSpawner;
import fr.jamailun.halystia.commands.CommandSetTag;
import fr.jamailun.halystia.commands.CommandSetXp;
import fr.jamailun.halystia.commands.CommandSkills;
import fr.jamailun.halystia.commands.CommandSummonMob;
import fr.jamailun.halystia.commands.CommandTitle;
import fr.jamailun.halystia.commands.ModifyOeilAntiqueCommand;
import fr.jamailun.halystia.commands.SystemCommand;
import fr.jamailun.halystia.donjons.DonjonManager;
import fr.jamailun.halystia.donjons.util.CommandBossDonjon;
import fr.jamailun.halystia.donjons.util.CommandDonjonPorte;
import fr.jamailun.halystia.donjons.util.CommandJoinDonjon;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawnerManager;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.jobs.JobsManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.quests.QuestManager;
import fr.jamailun.halystia.sql.temporary.DataHandler;
import fr.jamailun.halystia.titles.TitlesManager;

public class CommandsManager {

	public CommandsManager(HalystiaRPG main, DataHandler bdd, JobsManager jobs, TitlesManager titleMgr, DonjonManager donjonsMgr, NpcManager npcMgr, QuestManager questsMgr, MobManager mobMgr, MobSpawnerManager spawnerMgr) {
		new CommandClasse(main);
		new CommandQuests(main);
		new CommandTitle(main);
		new CommandPing(main);
		new CommandSkills(main);
		new CommandNpcTeleport(main, npcMgr);
		
		main.getCommand("create-shop-classe").setExecutor(new CommandCreateShop(main));
		main.getCommand("reload-shop-classe").setExecutor(new CommandReloadShops(main));
		new CommandPurge(main);
		
		new CommandCustomEnchant(main);
		new CommandGiveWater(main); // pour les cmdss blocks : ya pas de lézard.
		
		new CommandEditMobs(main);
		new CommandEditChunks(main);
		new CommandEditNPC(main, npcMgr);
		new CommandEditQuests(main, npcMgr, questsMgr, mobMgr);
		new CommandEditTitles(main, titleMgr);
		new CommandEditDonjons(main, donjonsMgr);
		new CommandEditEquipment(main);
		new CommandReloadSpells(main);
		
		new CommandSetRoi(main);
		new CommandSetXp(main);
		new CommandSetKarma(main);
		new CommandSetTag(main, bdd);
		new CommandSetChunk(main);
		new CommandSetSpawner(main, mobMgr, spawnerMgr);
		new CommandSetJob(main, jobs);
		
		new CommandGiveSpell(main);
		new CommandGivePotion(main);
		new CommandSummonMob(main, mobMgr);
		new CommandGiveCanne(main);
		new CommandGiveItems(main, jobs);
		
		main.getCommand("joindonjon").setExecutor(new CommandJoinDonjon(main));
		main.getCommand("donjonPorte").setExecutor(new CommandDonjonPorte(main));
		main.getCommand("donjonBoss").setExecutor(new CommandBossDonjon(main));
		
		ModifyOeilAntiqueCommand moaCmd = new ModifyOeilAntiqueCommand(main);
		main.getCommand("set-oeil-antique").setExecutor(moaCmd);
		main.getCommand("remove-oeil-antique").setExecutor(moaCmd);
		

		main.getCommand("systemctl").setExecutor(new SystemCommand(main));
	}
	
}