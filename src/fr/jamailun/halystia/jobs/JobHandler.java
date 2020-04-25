package fr.jamailun.halystia.jobs;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.jobs.recolte.JobRecolte;
import fr.jamailun.halystia.jobs.recolte.JobRecolteBloc;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.PlayerUtils;

public abstract class JobHandler extends FileDataRPG {

	private final Map<UUID, Integer> xp;
	
	private final JobName name;
	protected final JobData data;
	
	public JobHandler(String pathFolder, JobName name, JobData data) {
		super(pathFolder, "data_"+name.getConfigName());
		this.data = data;
		this.name = name;
		xp = new HashMap<>();
		for(String uid : config.getKeys(false))
			xp.put(UUID.fromString(uid), config.getInt(uid));
	}
	
	public boolean hasJob(Player p) {
		if(config == null) {
			System.err.println("ERREUR CONFIG NULL POUR JOBHANDLER " + getJobName());
			return false;
		}
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
	
	public JobRecolteBloc getBlockDataFromBlock(Block b, Material tool) {
		if( ! ( data instanceof JobRecolte ) )
			return null;
		return ((JobRecolte)data).getBlocData(b, tool);
	}
	
	public JobName getJobName() {
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
					ChatColor.GOLD + "" + ChatColor.DARK_GREEN + "+"+exp+"xp"
					+ ChatColor.BLUE + " |  "+name.getName()+"  |  "
					+ ChatColor.YELLOW + "("+(exp+currentXp)+" / " + getXpRequired(getLevel(currentXp+exp)+1) + ")"
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
	
	public List<ItemStack> getIcons(int levelMax) {
		List<ItemStack> list = new ArrayList<>();
		if(data instanceof JobRecolte) {
			for(JobRecolteBloc bl : ((JobRecolte)data).getData()) {
				if(bl.getLevelRequired() > levelMax)
					continue;
				ItemBuilder item = new ItemBuilder(bl.getResult());
				item.addLoreLine("Niveau requis : " + ChatColor.GREEN + bl.getLevelRequired());
				item.addLoreLine("Bloc : " + ChatColor.DARK_GREEN + bl.getType());
				item.addLoreLine("Outil : " + ChatColor.DARK_GREEN + bl.getToolType());
				list.add(item.toItemStack());
			}
			return list;
		}
		return list;
	}
	
}