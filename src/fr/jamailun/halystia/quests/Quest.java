package fr.jamailun.halystia.quests;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepBring;
import fr.jamailun.halystia.quests.steps.QuestStepKill;
import fr.jamailun.halystia.quests.steps.QuestStepSpeak;
import fr.jamailun.halystia.titles.Title;
import fr.jamailun.halystia.utils.FileDataRPG;

/**
 * Quest. Pick of differents QuestStep.
 * <br> Saved with the {@link fr.jamailun.halystia.utils.FileDataRPG} system.
 * @author jamailun
 * @see QuestManager
 * @see fr.jamailun.halystia.quests.steps.QuestStep QuestStep
 */
public class Quest extends FileDataRPG {
	
	private final String id;
	private String name;
	private int xp, level;
	private ItemStack[] loots;
	private QuestStep[] steps;
	private List<String> tagsGifts;
	
	private final HalystiaRPG main;
	private Messages intro;
	
	private boolean valid;
	private final NpcManager npcs;
	private final MobManager mobs;
	
	public Quest(String path, String id, HalystiaRPG main, NpcManager npcs, MobManager mobs) {
		super(path, id);
		this.main = main;
		this.id = id;
		this.npcs = npcs;
		this.mobs = mobs;
		
		loadData();
	}
	
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', name);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Quest)
			return ((Quest)o).id.equals(id);
		return false;
	}
	
	public String toString() {
		return "Quest[id="+id+"]";
	}
	
	private void loadData() {
		preloadData();
		name = config.getString("name");
		level = config.getInt("level");
		xp = config.getInt("gifts.xp");
		loots = new ItemStack[config.getInt("gifts.loots.n")];
		for(int i = 0; i < loots.length; i++)
			loots[i] = config.getItemStack("gifts.loots."+i);
		tagsGifts = config.getStringList("gifts.tags");

		valid = true;
		steps = new QuestStep[config.getInt("steps.n")];
		for(int i = 0; i < steps.length; i++)
			steps[i] = QuestStep.factory(config.getConfigurationSection("steps."+i), this, i, npcs, mobs);
		intro = new Messages(config.getStringList("intro"));
		
	}

	public int getXp() {
		return xp;
	}
	
	public List<String> getTagsGifts() {
		return new ArrayList<>(tagsGifts);
	}
	
	public void invalid() {
		valid = false;
	}
	
	public List<ItemStack> getLoots() {
		return Arrays.asList(loots);
	}
	
	private void preloadData() {
		if(!config.contains("name"))
			config.set("name", id);
		if(!config.contains("level"))
			config.set("level", -1);
		if(!config.contains("gifts.xp"))
			config.set("gifts.xp", -1);
		if(!config.contains("gifts.tags"))
			config.set("gifts.tags", new ArrayList<>());
		if(!config.contains("gifts.loots.n"))
			config.set("gifts.loots.n", 0);
		if(!config.contains("steps.n"))
			config.set("steps.n", 0);
		if(!config.contains("intro"))
			config.set("intro", new ArrayList<>());
		System.out.println("Lecture de la quete "+id+".");
		save();
	}
	
	public void addLoot(ItemStack item) {
		ItemStack[] tab = new ItemStack[loots.length + 1];
		for(int i = 0; i < loots.length; i++)
			tab[i] = loots[i];
		tab[loots.length] = item;
		loots = tab;
		updateLoots();
	}
	
	public boolean removeLoot(int slot) {
		if(slot < 0 || slot >= loots.length)
			return false;
		ItemStack[] tab = new ItemStack[loots.length - 1];
		boolean passed = false;
		for(int i = 0; i < tab.length; i++) {
			if(i == slot) {
				passed = true;
				continue;
			}
			if(passed) {
				tab[i] = loots[i+1];
				continue;
			}
			tab[i] = loots[i];
		}
		loots = tab;
		updateLoots();
		return true;
	}
	
	private void updateLoots() {
		synchronized (file) {
			config.set("gifts.loots", null);
			config.set("gifts.loots.n", loots.length);
			for(int i = 0; i < loots.length; i++) {
				config.set("gifts.loots."+i, loots[i]);
			}
			save();
		}
	}

	public void clearLoots() {
		loots = new ItemStack[0];
		updateLoots();
	}
	
	public Messages getIntro() {
		return intro;
	}
	
	public boolean isCorrect() {
		return steps.length > 0 && intro.getLenght() > 0 && !name.equals(id) && xp > -1 && level > -1 && valid;
	}
	
	public String getWhyNotCorrect() {
		if(isCorrect())
			return GREEN + "OK";
		if(!valid)
			return "NPC_ERROR";
		if(steps.length == 0)
			return "NO_STEP";
		if(intro.getLenght() == 0)
			return "NO_INTRO";
		if(name.equals(id))
			return "NO_RENAME";
		if(xp < 0)
			return "NO_EXP.";
		if(level < 0)
			return "NO_LEVEL";
		return DARK_RED+"UNKNOW_ERROR";
	}
	
	public int getHowManySteps() {
		return steps.length;
	}
	
	public boolean removeTagGift(String tag) {
		if( ! tagsGifts.contains(tag))
			return false;
		tagsGifts.remove(tag);
		synchronized (file) {
			config.set("gifts.tags", tagsGifts);
			save();
		}
		return true;
	}
	
	public boolean addTagGift(String tag) {
		if(tagsGifts.contains(tag))
			return false;
		tagsGifts.add(tag);
		synchronized (file) {
			config.set("gifts.tags", tagsGifts);
			save();
		}
		return true;
	}
	
	public void clearTagsGifts() {
		tagsGifts.clear();
		synchronized (file) {
			config.set("gifts.tags", tagsGifts);
			save();
		}
	}
	
	public String getID() {
		return id;
	}
	
	public boolean isvalid() {
		return valid;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void deleteData() {
		super.delete();
		valid = false;
	}
	
	public void changeLevel(int level) {
		this.level = level;
		synchronized (file) {
			config.set("level", level);
			save();
		}
	}
	
	public void changeXp(int xp) {
		this.xp = xp;
		synchronized (file) {
			config.set("gifts.xp", xp);
			save();
		}
	}
	
	public void rename(String name) {
		this.name = name;
		synchronized (file) {
			config.set("name", name);
			save();
		}
	}
	
	public int getDataForPlayer(Player p) {
		return main.getDataBase().getDataInQuest(p, this);
	}

	public void stepOver(Player p, int step) {
		main.getDataBase().updateStepInQuest(p, this, step+1);
		if(step >= getHowManySteps() - 1)
			completed(p);
		else
			p.sendMessage(steps[step+1].getObjectiveDescription());
	}
	
	public void completed(Player p) {
		//le step a déjà été mis au max :D
		p.sendMessage(GOLD + "" + BOLD + "Félicitation !" + YELLOW + "  Tu as terminé la quête "+getDisplayName()+YELLOW+" !");
		if(xp > 0)
			p.sendMessage(YELLOW + "Tu reçois " + LIGHT_PURPLE + xp + YELLOW + " points d'expérience de classe.");
		for(String tag : tagsGifts) {
			main.getDataBase().addTagToPlayer(p, tag);
			Title title = HalystiaRPG.getInstance().getTitlesManager().getTitleWithTag(tag);
			if(title != null)
				p.sendMessage(GREEN + "Tu obtiens le titre : " + title.getDisplayName() + GREEN + ".");
		}
		for(ItemStack item : loots) {
			p.sendMessage(YELLOW + "+" + GREEN + (item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase() : item.getType().toString().toLowerCase())
					+ YELLOW + " x" +item.getAmount());
			p.getInventory().addItem(item);
		}
		int tent = 5;
		while(main.getClasseManager().getPlayerData(p) == null) {
			System.err.println("Classe de " + p.getName() + " introuvable.");
			tent--;
			if(tent == 0) {
				p.sendMessage(ChatColor.RED+"Suite à une erreur interne, tu n'as pas obtenu les " + xp + " que tu aurais dû recevoir. Désolé.");
				return;
			}
		}
		main.getClasseManager().getPlayerData(p).addXp(xp);
	}

	public void updateDataForPlayer(Player p, int killed) {
		main.getDataBase().updateDataInQuest(p, this, killed);
	}

	public QuestStep[] getSteps() {
		return Arrays.copyOf(steps, steps.length);
	}
	
	public QuestStep getStep(int step) {
		if(step < 0 || step >= steps.length)
			return null;
		return steps[step];
	}

	public boolean playerHasLevel(Player p) {
		if(main.getClasseManager().getPlayerData(p) == null) {
			System.err.println("ERREUR POUR LA CLASSE");
			return false;
		}
		if(main.getDataBase().getAllQuests(p).contains(this))
			return false;
		if(level < 0)
			return false;
		if(level == 0)
			return true;
		return main.getClasseManager().getPlayerData(p).getLevel() >= level;
	}
	
	public void saveIntro() {
		synchronized (config) {
			config.set("intro", intro.getDialog());
			save();
		}
	}

	public int sendIntroduction(RpgNpc npc, Player p) {
		List<String> msgs = intro.getDialog();
		for(int i = 0; i < msgs.size(); i++) {
			final int j = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					npc.sendMessage(p, ChatColor.translateAlternateColorCodes('&', msgs.get(j)));
				}
			}.runTaskLater(HalystiaRPG.getInstance(), NpcManager.TIME_BETWEEN_MESSAGES * i);
		}
		return msgs.size();
	}

	public void startQuest(Player p) {
		if(main.getDataBase().getStepInQuest(p, this) >= 0) {
			p.sendMessage(DARK_RED + "Une erreur est survenue quete " + id + ". Step attendu: -1. Step obtenu :" + main.getDataBase().getStepInQuest(p, this));
			return;
		}
		p.sendMessage(DARK_PURPLE + "" + BOLD + "QUÊTE OBTENUE : " + getDisplayName() + DARK_PURPLE + " !");
		main.getDataBase().updateStepInQuest(p, this, 0);
		p.sendMessage(steps[0].getObjectiveDescription());
	}

	public void addStepSpeak(RpgNpc cible) {
		int newNumberSteps = steps.length + 1;
		QuestStep[] tab = new QuestStep[newNumberSteps];
		for(int i = 0; i < steps.length; i++)
			tab[i] = steps[i];
		
		synchronized (file) {
			QuestStepSpeak.serialize(cible, config.createSection("steps."+steps.length));
			config.set("steps.n", newNumberSteps);
			save();
		}

		final Quest quest = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				tab[steps.length] = new QuestStepSpeak(config.getConfigurationSection("steps."+steps.length), quest, steps.length, npcs, mobs);
				steps = tab;
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 10L);
	}
	
	public void addStepBring(RpgNpc cible, ItemStack item) {
		int newNumberSteps = steps.length + 1;
		QuestStep[] tab = new QuestStep[newNumberSteps];
		for(int i = 0; i < steps.length; i++)
			tab[i] = steps[i];
		
		synchronized (file) {
			QuestStepBring.serialize(cible, item, config.createSection("steps."+steps.length));
			config.set("steps.n", newNumberSteps);
			save();
		}
		final Quest quest = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				tab[steps.length] = new QuestStepBring(config.getConfigurationSection("steps."+steps.length), quest, steps.length, npcs, mobs);
				steps = tab;
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 10L);
		
	}
	
	public void addStepKill(String mob, int howMany) {
		int newNumberSteps = steps.length + 1;
		QuestStep[] tab = new QuestStep[newNumberSteps];
		for(int i = 0; i < steps.length; i++)
			tab[i] = steps[i];
		
		synchronized (file) {
			QuestStepKill.serialize(mob, howMany, config.createSection("steps."+steps.length));
			config.set("steps.n", newNumberSteps);
			save();
		}
		final Quest quest = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				tab[steps.length] = new QuestStepKill(config.getConfigurationSection("steps."+steps.length), quest, steps.length, npcs, mobs);
				steps = tab;
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 10L);
	}

	
	public boolean destroyStep(int slot) {
		if(slot < 0 || slot >= steps.length)
			return false;
		synchronized (file) {
			config.set("steps", null);
			config.set("steps.n", steps.length - 1);
			QuestStep[] tab = new QuestStep[steps.length - 1];
			boolean passed = false;
			for(int i = 0; i < steps.length; i++) {
				if(i == slot) {
					passed = true;
					continue;
				}
				QuestStep step = null;
				int j = passed ? i+1 : i;
				QuestStep old = steps[j];
				ConfigurationSection section = config.createSection("steps."+j);
				switch(old.getType()) {
				case BRING:
					QuestStepBring stb = (QuestStepBring) old;
					QuestStepBring.serialize(stb.getTarget(), stb.getItem(), section);
					step = new QuestStepBring(section, this, j, npcs, mobs);
					break;
				case KILL:
					QuestStepKill stk = (QuestStepKill) old;
					QuestStepKill.serialize(stk.getMobName(), stk.getHowManyToKill(), section);
					step = new QuestStepKill(section, this, j, npcs, mobs);
					break;
				case SPEAK:
					QuestStepSpeak stp = (QuestStepSpeak) old;
					QuestStepSpeak.serialize(stp.getTarget(), section);
					step = new QuestStepSpeak(section, this, j, npcs, mobs);
					break;
				default:
					return false;
				}
				tab[j] = step;
			}
			save();
			steps = tab;
		}
		return true;
	}
	
	public boolean destroyLastStep() {
		if(steps.length == 0)
			return false;
		synchronized (file) {
			config.set("steps", null);
			config.set("steps.n", steps.length - 1);
			QuestStep[] tab = new QuestStep[steps.length - 1];
			for(int i = 0; i < tab.length; i++) {
				QuestStep step = null;
				QuestStep old = steps[i];
				ConfigurationSection section = config.createSection("steps."+i);
				switch(old.getType()) {
				case BRING:
					QuestStepBring stb = (QuestStepBring) old;
					QuestStepBring.serialize(stb.getTarget(), stb.getItem(), section);
					step = new QuestStepBring(section, this, i, npcs, mobs);
					break;
				case KILL:
					QuestStepKill stk = (QuestStepKill) old;
					QuestStepKill.serialize(stk.getMobName(), stk.getHowManyToKill(), section);
					step = new QuestStepKill(section, this, i, npcs, mobs);
					break;
				case SPEAK:
					QuestStepSpeak stp = (QuestStepSpeak) old;
					QuestStepSpeak.serialize(stp.getTarget(), section);
					step = new QuestStepSpeak(section, this, i, npcs, mobs);
					break;
				default:
					return false;
				}
				tab[i] = step;
			}
			save();
			steps = tab;
		}
		return true;
	}

	public void setDialog(int id, Messages msg) {
		if(id < 0 || id >= steps.length)
			return;
		synchronized (file) {
			getStep(id).setMessages(msg);
			config.set("steps."+id+".messages", msg.getDialog());
			save();
		}
	}
	
	public void setIntro(Messages intro) {
		this.intro = intro;
		synchronized (file) {
			config.set("intro", intro.getDialog());
			save();
		}
	}

	public boolean clearLootStep(int id) {
		if(id < 0 || id >= steps.length)
			return false;
		steps[id].resetLoot();
		synchronized (file) {
			config.set("steps."+id+".loot", null);
			save();
		}
		return true;
	}
	
	public boolean setLootStep(int id, ItemStack item) {
		if(id < 0 || id >= steps.length)
			return false;
		steps[id].setLoot(item);
		synchronized (file) {
			config.set("steps."+id+".loot", item);
			save();
		}
		return true;
	}
	
	public ItemStack getLootStep(int id) {
		if(id < 0 || id >= steps.length)
			return null;
		return steps[id].getLoot();
	}
}