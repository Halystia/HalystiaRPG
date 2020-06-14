package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.BLACK;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_BLUE;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobType;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.players.SkillSet;
import fr.jamailun.halystia.players.SoulManager;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.titles.Title;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class MainClasseGUI extends MenuGUI {

	public final static int SLOT_HEAD = 4;
	public final static int SLOT_CLASSE = 10;
	public final static int SLOT_AMES = 11;
	public final static int SLOT_QUESTS = 12;
	public final static int SLOT_TITLES = 13;
	public final static int SLOT_SKILLS = 14;

	public final static int SLOT_JOBS1 = 15;
	
	private JobType j1 = null, j2 = null;
	private Classe classe;
	private Player p;
	
	public MainClasseGUI(Player p) {
		super(BLACK + "Profil de " + DARK_BLUE + p.getName(), 9*3, HalystiaRPG.getInstance());
		this.p = p;
		PlayerData pc = HalystiaRPG.getInstance().getClasseManager().getPlayerData(p);
		classe = pc.getClasse();
		
		//void
		for(int i = 0; i < 9*3; i++)
			addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(""+WHITE).toItemStack(), i);
		
		//profil général
		ItemBuilder head = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(p.getName());
		head.setName(GREEN + p.getName());
		if(classe != Classe.NONE) {
			int lvl = pc.getLevel();
			head.addLoreLine(BLUE + "Rang : " + LIGHT_PURPLE + classe.getTitlename(lvl));
			head.addLoreLine(BLUE + "Niveau : " + DARK_PURPLE + lvl);
			head.addLoreLine(DARK_GRAY + "Expérience : " + pc.getExpAmount() + "/" + pc.getExpRequired(lvl+1));
			head.addLoreLine(pc.getExpBar(20));
			head.addLoreLine(" ");
			head.addLoreLine(LIGHT_PURPLE + "Karma : " + pc.getNiceKarma() + " points");
			if(pc.getCurrentKarma() <= -300)
				head.addLoreLine(RED + "Vous êtes un criminel.");
			else if(pc.getCurrentKarma() >= 300)
				head.addLoreLine(GREEN + "Vous êtes un bienfaiteur.");
			
		} else {
			head.addLoreLine(GRAY + "Aucune classe");
		}
		head.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		addOption(head.toItemStack(), SLOT_HEAD);
		
		//Classe
		if(classe == Classe.NONE) {
			addOption(new ItemBuilder(Material.DEAD_BUSH).setName(GOLD+"Choisir sa classe").toItemStack(), SLOT_CLASSE);
		} else {
			addOption(new ItemBuilder(Material.TNT).setName(DARK_RED+"Changer de classe").setLore(RED+"Attention ! Cela est définitif", RED+"Il vous faut" + DARK_RED + ""+ BOLD + " 10 " + RED + "blocs d'émeraude pour changer.").toItemStack(), SLOT_CLASSE);
		}
		
		//Âmes
		ItemBuilder temps = new ItemBuilder(HalystiaRPG.getInstance().getSoulManager().getItemForPlayer(p));
		if(temps.toItemStack().getAmount() < 3) {
			temps.addLoreLine(GRAY+"Prochaine âme dans " + DARK_GRAY + "(Chargement)"+GRAY+".");
		}
		addOption(temps.toItemStack(), SLOT_AMES);
		regenTimerAme(true);
		
		//Quetes
		ItemBuilder quests = new ItemBuilder(Material.BOOKSHELF).setName(YELLOW+""+BOLD+"Quêtes");
		int currents = HalystiaRPG.getInstance().getQuestManager().getPlayerData(p).getOnGoingQuests().size();
		if(currents == 0)
			quests.setLore(GRAY+""+ITALIC+"Aucune quête en cours.");
		else
			quests.setLore(GREEN+"Actuellement "+GOLD+currents+GREEN+" quête"+(currents>1?"s":"")+" en cours.");
		addOption(quests.toItemStack(), SLOT_QUESTS);
		
		//Titres
		ItemBuilder titles = new ItemBuilder(Material.OAK_SIGN).setName(LIGHT_PURPLE+""+BOLD+"Titres");
		String titre = HalystiaRPG.getInstance().getDataBase().getCurrentTitleOfPlayer(p);
		if(titre != null) {
			Title tt = HalystiaRPG.getInstance().getTitlesManager().getTitleWithTag(titre);
			if(tt != null)
				titles.setLore(YELLOW+"Actuel : " + tt.getDisplayName());
			else
				titles.setLore(GRAY+"(Aucun selectionné)");
		} else {
			titles.setLore(GRAY+"(Aucun selectionné)");
		}
		addOption(titles.toItemStack(), SLOT_TITLES);
		
		//Skills
		int allPoints = pc.getLevel() / 2;
		SkillSet skillSet = pc.getSkillSetInstance();
		int remaining = allPoints - skillSet.getTotalPoints();
		ItemBuilder skills = new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).setName(LIGHT_PURPLE+""+BOLD+"Skills passifs");
		skills.addLoreLine(GRAY+"Points : " + (remaining > 0 ? GREEN : RED) + remaining +GRAY+"/"+allPoints);
		skills.addLoreLine(DARK_GRAY+"Force : " + GOLD + skillSet.getLevel(SkillSet.SKILL_FORCE));
		skills.addLoreLine(DARK_GRAY+"Intelligence : " + GOLD + skillSet.getLevel(SkillSet.SKILL_INTELLIGENCE));
		skills.addLoreLine(DARK_GRAY+"Constitution : " + GOLD + skillSet.getLevel(SkillSet.SKILL_CONSTITUTION));
		skills.addLoreLine(DARK_GRAY+"Agilité : " + GOLD + skillSet.getLevel(SkillSet.SKILL_AGILITE));
		addOption(skills.toItemStack(), SLOT_SKILLS);
		
		//JOBS
		int jobSlot = SLOT_JOBS1;
		for(JobType job : HalystiaRPG.getInstance().getJobManager().getJobsOfPlayer(p)) {
			if(job == null)
				continue;
			
			addOption(job.getIcon(p), jobSlot);
			
			if(jobSlot == SLOT_JOBS1)
				j1 = job;
			else if(jobSlot == SLOT_JOBS1 + 1)
					j2 = job;
			jobSlot++;
		}
		
		if(jobSlot == SLOT_JOBS1) {
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.DARK_GRAY + "Pas de métier").toItemStack(), jobSlot);
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.DARK_GRAY + "Pas de métier").toItemStack(), jobSlot+1);
		} else if(jobSlot == SLOT_JOBS1 + 1) {
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.DARK_GRAY + "Pas de métier").toItemStack(), jobSlot);
		}
		
		show(p);
	}
	
	private void regenTimerAme(boolean instantaneous) {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			@Override
			public void run() {
				ItemBuilder temps = new ItemBuilder(HalystiaRPG.getInstance().getSoulManager().getItemForPlayer(p));
				if(temps.toItemStack().getAmount() < 3) {
					final int secsEcoulees = HalystiaRPG.getInstance().getDataBase().getLastSoulRefresh(p);
					int secondes = SoulManager.SECONDS_BETWEEN_REFRESH - secsEcoulees;
					int minutes = 0;
					while(secondes >= 60) {
						minutes++;
						secondes -= 60;
					}
					String tStr = (minutes == 0 ? "" : minutes+":") + (secondes > 9 ? "" : "0") + secondes + "s";
					temps.addLoreLine(GRAY+"Prochaine âme dans " + BLUE + tStr + GRAY+".");
					if(minutes <= 0 && secondes <= 1) {
						HalystiaRPG.getInstance().getSoulManager().tryRefreshSoul(player);
					}
				}
				addOption(temps.toItemStack(), SLOT_AMES);
				regenTimerAme(false);
			}
		}, (instantaneous ? 1L : 20L));
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null)
			return;
		if(e.getCurrentItem().getType() == Material.AIR)
			return;
		e.setCancelled(true);
		if(classe == Classe.NONE) {
			if(e.getSlot() == SLOT_CLASSE) {
				HalystiaRPG.getInstance().getChooseClasseGui().openGui(p);
			}
		} else {
			if(e.getSlot() == SLOT_CLASSE) {
				Trade trade = new Trade( new ItemBuilder(Material.PAPER)
								.setName(YELLOW+"Reçu d'oubli de classe")
								.addLoreLine(GRAY+"Classe oubliée : " + WHITE + classe.getName())
								.addLoreLine(GRAY+"Expérience perdue : " + WHITE + HalystiaRPG.getInstance().getClasseManager().getPlayerData(p).getExpAmount())
								.addLoreLine(GRAY+"Signé le : " + WHITE + new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
								.addLoreLine(GRAY+"Signé par : " + WHITE + p.getName())
								.toItemStack(),
								new ItemStack(Material.EMERALD_BLOCK, 10)
							);
				/*Trade trade = new Trade("null",
						classe, 
						new ItemBuilder(Material.PAPER)
								.setName(YELLOW+"Reçu d'oubli de classe")
								.addLoreLine(GRAY+"Classe oubliée : " + WHITE + classe.getName())
								.addLoreLine(GRAY+"Expérience perdue : " + WHITE + HalystiaRPG.getInstance().getClasseManager().getPlayerData(p).getExpAmount())
								.addLoreLine(GRAY+"Signé le : " + WHITE + new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
								.addLoreLine(GRAY+"Signé par : " + WHITE + p.getName())
								.toItemStack(),
						Arrays.asList(new ItemStack(Material.EMERALD_BLOCK, 10)),
						0
				);*/
				if(trade.canAfford(p)) {
					openQuitClasseGUI(trade);
					return;
				}
				p.sendMessage(HalystiaRPG.PREFIX+RED+"Tu n'as pas assez d'argent sur toi !");
			}
		}
		if(e.getSlot() == SLOT_QUESTS)
			new MainQuestsGUI(p);
		else if(e.getSlot() == SLOT_TITLES)
			new MainTitlesGUI(p);
		else if(e.getSlot() == SLOT_SKILLS)
			new SkillsGUI(p);
		else if(e.getSlot() == SLOT_JOBS1 && j1 != null )
			new MainJobGUI(p, j1, HalystiaRPG.getInstance().getJobManager());
		else if(e.getSlot() == SLOT_JOBS1 + 1 && j2 != null )
			new MainJobGUI(p, j2, HalystiaRPG.getInstance().getJobManager());
	}
	
	private void openQuitClasseGUI(Trade trade) {
		final HalystiaRPG api = HalystiaRPG.getInstance();
		MenuGUI gui = new MenuGUI(DARK_RED + "Quitter définitivement ta classe ?", 9*2, api) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getCurrentItem() == null)
					return;
				if(e.getCurrentItem().getItemMeta() == null)
					return;
				if( ! e.getCurrentItem().getItemMeta().hasDisplayName())
					return;
				Player p = (Player) e.getWhoClicked();
				Material mat = e.getCurrentItem().getType();
				if(mat == Material.LIME_CONCRETE) {
					if(trade.trade(p)) {
						api.getDataBase().changePlayerClasse(p, Classe.NONE);
						api.getClasseManager().changePlayerClasse(p, Classe.NONE);
						p.sendMessage(HalystiaRPG.PREFIX+LIGHT_PURPLE+""+BOLD + "Succès." + DARK_RED + "Tu as bien oublié ta classe.");
						p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.0f, 1.5f);
						p.closeInventory();
					} else {
						p.sendMessage(HalystiaRPG.PREFIX+RED + "Une erreur est survenue. Aviez-vous assez d'argent ?");
					}
				} else if(mat == Material.RED_CONCRETE) {
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, .5f);
					new MainClasseGUI(p);
				} else if(mat == Material.ARROW) {
					new MainClasseGUI(p);
				}
			}
		};
		for(int i = 0; i < 18; i++)
			gui.addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(), i);
		gui.addOption(
				new ItemBuilder(Material.PAPER)
				.setName(RED+"Attention !")
				.addLoreLine(GRAY+"Ce choix est définitif...")
				.addLoreLine(GRAY+"Tu perdras toute ton expérience !")
				.toItemStack()
		, 4);
		
		gui.addOption(new ItemBuilder(Material.LIME_CONCRETE).setName(DARK_GREEN+""+BOLD+"OUI").toItemStack(), 2);
		gui.addOption(new ItemBuilder(Material.RED_CONCRETE).setName(DARK_RED+""+BOLD+"NON").toItemStack(), 6);
		
		gui.addOption(new ItemBuilder(Material.ARROW).setName(RED+"Retour").toItemStack(), 17);
		
		gui.show(p);
	}
}