package fr.jamailun.halystia.jobs;

import static org.bukkit.ChatColor.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.PlayerUtils;

public class JobType extends FileDataRPG {
	
	public final static int MAX_LEVEL = 5;
	
	private Material craftBlock;
	private JobCraftGUI craftGUI;
	private final Map<UUID, Integer> xp;
	private final String name;
	private final String nameUp;
	private final JobCategory category;
	private final Material[] icons;
	
	private final JobsManager manager;
	public JobType(String path, String name, JobCategory category, JobsManager manager) {
		super(path, name);
		this.name = name;
		this.manager = manager;
		String s1 = name.substring(0, 1).toUpperCase();
		nameUp = s1 + name.substring(1);
		
		this.category = category;
		icons = new Material[MAX_LEVEL];
		for(int i = 0; i < MAX_LEVEL; i++)
			icons[i] = Material.BARRIER;
		xp = new HashMap<>();
		for(String uid : config.getKeys(false))
			xp.put(UUID.fromString(uid), config.getInt(uid));
	}
	
	public boolean equals(Object o) {
		if(o instanceof JobType)
			return ((JobType)o).name.equals(name);
		return false;
	}
	
	/**
	 * When right clic ont this bloc, open {@link #getCraftGUI()} GUI.
	 * @return the Craft block.
	 */
	public Material getCraftBlock() {
		return craftBlock;
	}
	
	public JobCraftGUI getCraftGUI() {
		return craftGUI;
	}
	
	public void changeCraftGUI(Material blockToCraft, JobCraftGUI craftGUI) {
		this.craftBlock = blockToCraft;
		this.craftGUI = craftGUI;
	}

	public JobCategory getCategory() {
		return category;
	}
	
	public boolean hasJob(Player p) {
		if(xp.containsKey(p.getUniqueId()))
			return xp.get(p.getUniqueId()) != -1;
		return false;
	}
	
	public void saveData() {
		synchronized (file) {
			xp.forEach((uid, xxp) -> {
				if(xxp == -1)
					config.set(uid.toString(), null);
				else
					config.set(uid.toString(), xxp);
			});
			save();
		}
	}
	
	public String getJobName() {
		return name;
	}

	public int getPlayerExp(Player p) {
		if(xp.containsKey(p.getUniqueId()))
			return xp.get(p.getUniqueId());
		return -1;
	}
	
	public int getLevel(int exp) {
		double level = (double)exp / 500.0;
		if(level < 1)
			level = 1;
		if(level > 15)
			level = 15;
		return (int) level;
	}
	
	public int getXpRequired(int level) {
		return level * 500;
	}
	
	public String getPercentBar(int currentXp, int currentLvl) {
		if(currentLvl == 15)
			return GOLD + "Niveau max !";
		double lvlN0 = currentLvl == 1 ? 0 : getXpRequired(currentLvl);
		double lvlN1 = getXpRequired(currentLvl + 1);
		double filled = currentXp - lvlN0;
		double upper = lvlN1 - lvlN0;
		double percent = filled / upper;
		
		StringBuilder builder = new StringBuilder(DARK_GRAY+"[");
		for(int i = 1; i <= BAR_SIZE; i++) {
			double currentPercent = ((double)i) / ((double)BAR_SIZE);
			if(currentPercent <= percent)
				builder.append(GREEN+BAR_CHAR);
			else
				builder.append(GRAY+BAR_CHAR);
		}
		builder.append(DARK_GRAY+"]");
		return builder.toString();
	}
	
	public final static String BAR_CHAR = new String(Character.toChars(9632));
	public final static int BAR_SIZE = 10;
	
	public void addExp(int exp, Player p) {
		if(hasJob(p)) {
			int currentXp = getPlayerExp(p);
			xp.replace(p.getUniqueId(), exp + currentXp);
			new PlayerUtils(p).sendActionBar(
					GOLD + "" + DARK_GREEN + "+"+exp+"xp"
					+ BLUE + " |  "+getJobNameMajor()+"  |  "
					+ YELLOW + "("+(exp+currentXp)+" / " + getXpRequired(getLevel(currentXp+exp)+1) + ")"
			);
		}
	}
	
	public int getPlayerLevel(Player p) {
		return getLevel(getPlayerExp(p));
	}

	public void registerPlayer(Player p) {
		if( ! hasJob(p))
			xp.put(p.getUniqueId(), 0);
	}
	
	public void unregisterPlayer(Player p) {
		if( hasJob(p) )
			xp.put(p.getUniqueId(), -1);
	}
	
	public ItemStack getIcon(Player p) {
		if( ! hasJob(p) )
			return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(GRAY + "Vous n'avez pas le métier").toItemStack();
		int xp = getPlayerExp(p);
		int level = getLevel(xp);
		int idLevel = level - 1;
		if(idLevel < 0)
			idLevel = 0;
		else if(idLevel >= MAX_LEVEL)
			idLevel = MAX_LEVEL - 1;
		
		ItemBuilder builder = new ItemBuilder(icons[idLevel]);
		
		
		builder.setName(getJobNameMajor());
		builder.setLore(GRAY + "Niveau "+level);
		builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		if(level == MAX_LEVEL) {
			builder.shine();
		} else {
			builder.addLoreLine(getPercentBar(xp, level));
			builder.addLoreLine(xp+"/"+getXpRequired(level+1)+" xp");
		}
		return builder.toItemStack();
	}

	public String getJobNameMajor() {
		return category.getColor() + nameUp;
	}
	
	public void forceExp(Player p, int exp) {
		if(hasJob(p)) {
			xp.replace(p.getUniqueId(), exp);
		}
	}
	
	public void setIcon(int level, Material type) {
		int i = level - 1;
		if(i < 0 || i >= MAX_LEVEL)
			throw new IllegalArgumentException("Level uncorrect. Must be between 1 and " + MAX_LEVEL+".");
		icons[i] = type;
	}

	public List<JobCraft> getCrafts(Player p) {
		int level = getPlayerLevel(p);
		return manager.getCraftsManager().getCrafts(this).stream().filter(craft -> craft.getLevel() <= level).collect(Collectors.toList());
	}

	public void openJobInventory(Player p) {
		if(hasJob(p))
			craftGUI.openGUItoPlayer(p);
	}
}