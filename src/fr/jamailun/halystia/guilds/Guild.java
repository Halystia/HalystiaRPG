package fr.jamailun.halystia.guilds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.Levelable;
import fr.jamailun.halystia.utils.MenuGUI;

public class Guild extends FileDataRPG implements Levelable {
	
	public static final int[] TAG_LENGHT = {3, 4};
	
	private String name, tag;
	private Map<UUID, GuildRank> members;
	private int maxMembers = 5;
	private Set<GuildInvite> pendingInvite = new HashSet<>();
	private boolean pvp = false;
	
	private final GuildChest chest;
	private int powerToPutItems = 1, powerToGetItems = 50;
	private int chestPages = 1;
	private int level = 1, xp = 0;
	
	public Guild(String path, String fileName) {
		super(path, fileName);
		members = new HashMap<>();
		loadFile();
		
		chest = new GuildChest(this, config.getConfigurationSection("chest.pages"));
	}
	
	public Guild(String path, String fileName, Player creator, String name) {
		super(path, fileName);
		members = new HashMap<>();
		members.put(creator.getUniqueId(), GuildRank.MASTER);
		this.name = name;
		this.tag = name.substring(0, TAG_LENGHT[TAG_LENGHT.length - 1]-1).toUpperCase();
		config.set("name", name);
		config.set("tag", tag);
		config.set("xp", 0);
		config.set("members", convertMembersToList(members));
		config.set("created", System.currentTimeMillis());
		config.set("allows.pvp", false);
		config.set("chest.pages.number", 1);
		save();
		
		chest = new GuildChest(this, config.getConfigurationSection("chest.pages"));
	}
	
	void saveChest(MenuGUI gui, int page) {
		synchronized (config) {
			for(int i = 0; i < gui.getSize() - 9; i++) {
				config.set("chest.pages."+page+"."+i, gui.getInventory().getItem(i));
			}
			save();
		}
	}
	
	public void addExp(Player player, int exp) {
		if(exp < 0)
			return;
		xp += exp;
		int newLevel = getLevelWithExp(xp);
		if(newLevel > level) {
			level = newLevel;
			sendMessageToMembers(getTag() + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "La guilde passe niveau " + ChatColor.GOLD + "" + ChatColor.BOLD + newLevel + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + " !");
			maxMembers = 5 + (level * 2);
		}
	}
	
	public void upgradeGuildChest() {
		if(chestPages >= 10)
			return;
		chestPages ++;
		sendMessageToMembers(getTag() + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Le coffre de guilde a été amélioré et passe niveau " + chestPages + " !");
		synchronized (config) {
			config.set("chest.pages.number", chestPages);
			save();
		}
		chest.addPage();
	}
	
	@Override
	public int getLevelWithExp(int exp) {
		return (int) Math.max(1, 0.5 * Math.pow(exp, 0.12));
	}
	
	void saveXp() {
		synchronized (config) {
			config.set("xp", xp);
			save();
		}
	}
	
	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public int getExpAmount() {
		return xp;
	}
	
	public void playerRequestOpenChest(Player player) {
		int power = getPlayerRank(player).getPower();
		if(powerToPutItems > power) {
			player.sendMessage(getTag() + ChatColor.RED + "Il faut un rang supérieur pour ouvrir le coffre de guilde.");
			return;
		}
		chest.openPlayer(player, power >= powerToGetItems);
	}
	
	public List<String> getOfflinePlayersNames() {
		return members.keySet().stream().map(id -> Bukkit.getOfflinePlayer(id).getName()).collect(Collectors.toList());
	}
	
	public void setPvp(boolean pvp) {
		if(pvp != this.pvp)
			sendMessageToMembers(getTag() + ChatColor.YELLOW + "" + ChatColor.BOLD + "Nouvelle règle pour le PvP : " + (pvp ? ChatColor.GREEN + "autorisé" : ChatColor.RED + "interdit")+ ChatColor.YELLOW + "" + ChatColor.BOLD +".");
		
		this.pvp = pvp;
		synchronized (config) {
			config.set("allows.pvp", pvp);
			save();
		}
	}
	
	public String getMasterName() {
		for(UUID uuid : members.keySet()) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			if(op == null)
				break;
			return op.getName();
		}
		return ChatColor.DARK_RED + "Erreur";
	}
	
	public String getGuildName() {
		return name;
	}
	
	public boolean isInTheGuild(Player player) {
		return members.containsKey(player.getUniqueId());
	}
	
	public boolean isInTheGuild(UUID uuid) {
		return members.containsKey(uuid);
	}
	
	public int getHowManyChestPages() {
		return chestPages;
	}
	
	public void addNewPage() {
		if(chestPages > 10)
			return;
		chestPages ++;
		synchronized (config) {
			config.set("chest.pages.number", chestPages);
			save();
		}
	}
	
	public int getHowManyItemsInChest() {
		return chest.getHowManyItems();
	}
	
	public GuildResult addPlayerToGuild(Player player) {
		if(isInTheGuild(player))
			return GuildResult.ALREADY_HERE;
		if(members.size() >= maxMembers)
			return GuildResult.GUILD_FULL;
		members.put(player.getUniqueId(), GuildRank.MEMBER);
		saveMembers();
		return GuildResult.SUCCESS;
	}
	
	public GuildResult changeTag(String tag) {
		tag = tag.toUpperCase(Locale.FRANCE);
		if(tag.length() < TAG_LENGHT[0] || tag.length() > TAG_LENGHT[TAG_LENGHT.length - 1])
			return GuildResult.WRONG_TAG_SIZE;
		if(HalystiaRPG.getInstance().getGuildManager().tagExists(tag))
			return GuildResult.TAG_ALREADY_EXISTS;
		this.tag = tag;
		synchronized (config) {
			config.set("tag", tag);
			save();
		}
		return GuildResult.SUCCESS;
	}
	
	public boolean allowsPVP() {
		return pvp;
	}
	
	public String getPureTag() {
		return tag;
	}
	
	public GuildRank getPlayerRank(Player player) {
		GuildRank rank = members.get(player.getUniqueId());
		return rank == null ? GuildRank.NOT_A_MEMBER : rank;
	}
	
	public GuildRank getPlayerRank(UUID uuid) {
		GuildRank rank = members.get(uuid);
		return rank == null ? GuildRank.NOT_A_MEMBER : rank;
	}
	
	public GuildResult promote(UUID uuid) {
		if( ! isInTheGuild(uuid))
			return GuildResult.PLAYER_NOT_HERE;
		GuildRank current = members.get(uuid);
		if(current == GuildRank.MASTER)
			return GuildResult.IS_ALREADY_MASTER;
		if(current == GuildRank.RIGHT_ARM)
			return GuildResult.CAN_ONLY_HAVE_ONE_MASTER;
		if(current == GuildRank.CAPITAIN && guildHasRightArm())
			return GuildResult.CAN_ONLY_HAVE_RIGHT_ARM;
		members.replace(uuid, current.promote());
		saveMembers();
		return GuildResult.SUCCESS;
	}
	
	public GuildResult demote(UUID uuid) {
		if( ! isInTheGuild(uuid))
			return GuildResult.PLAYER_NOT_HERE;
		GuildRank current = members.get(uuid);
		if(current == GuildRank.MASTER)
			return GuildResult.MASTER_CANNOT_BE_DEMOTE;
		if(current == GuildRank.MEMBER)
			return GuildResult.IS_ALREADY_MEMBER;
		members.replace(uuid, current.demote());
		saveMembers();
		return GuildResult.SUCCESS;
	}
	
	public GuildInvite generateInvite(Player source) {
		if( ! isInTheGuild(source))
			return null;
		if( ! hasPermissions(source, GuildRank.RIGHT_ARM))
			return null;
		GuildInvite invite = new GuildInvite(source, this);
		pendingInvite.add(invite);
		return invite;
	}
	
	public boolean isInviteValid(String token) {
		return pendingInvite.stream().anyMatch(gi -> gi.getToken().equals(token));
	}
	
	public GuildResult broadcast(Player source, String message) {
		if( ! isInTheGuild(source))
			return GuildResult.PLAYER_NOT_HERE;
		if( ! hasPermissions(source, GuildRank.CAPITAIN))
			return GuildResult.NEED_TO_BE_CAPTAIN;
		message = getTag() + ChatColor.WHITE + "" + ChatColor.BOLD + source.getName() + " > " + ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.translateAlternateColorCodes('&', message);
		sendMessageToMembers(message);
		return GuildResult.SUCCESS;
	}
	
	public GuildResult internalMessage(Player source, String message) {
		if( ! isInTheGuild(source))
			return GuildResult.PLAYER_NOT_HERE;
		message = ChatColor.GRAY + "(Guilde) " + ChatColor.WHITE + source.getName() + ChatColor.GRAY + " > " + message;
		sendMessageToMembers(message);
		return GuildResult.SUCCESS;
	}
	
	public void sendMessageToMembers(String message) {
		for(UUID uuid : members.keySet()) {
			Player to = Bukkit.getPlayer(uuid);
			if(to != null)
				to.sendMessage(message);
		}
	}
	
	public String getTag() {
		return ChatColor.GRAY + "[" + ChatColor.WHITE + tag + ChatColor.GRAY + "] " + ChatColor.WHITE;
	}
	
	public boolean hasPermissions(Player player, GuildRank minimal) {
		if( ! members.containsKey(player.getUniqueId()))
			return false;
		return members.get(player.getUniqueId()).getPower() >= minimal.getPower();
	}
	
	public List<String> getMembersDisplay() {
		List<String> list = new ArrayList<>();
		members.forEach((id, rank) -> {
			String name = Bukkit.getOfflinePlayer(id).getName();
			list.add(rank.getColor() + ChatColor.BOLD + name + rank.getColor() + " : " + rank.toString());
		});
		return list;
	}
	
	private void saveMembers() {
		synchronized (config) {
			config.set("members", convertMembersToList(members));
			save();
		}
	}
	
	public boolean guildHasRightArm() {
		return members.containsValue(GuildRank.RIGHT_ARM);
	}
	
	private void loadFile() {
		name = config.getString("name");
		xp = config.getInt("xp");
		level = getLevelWithExp(xp);
		if(config.contains("tag"))
			tag = config.getString("tag");
		else
			tag = name.substring(0, TAG_LENGHT[TAG_LENGHT.length - 1] - 1).toUpperCase();
		members = convertMembersFromList(config.getStringList("members"));
		pvp = config.getBoolean("allows.pvp");
		if( ! config.contains("chest.pages.number"))
			config.set("chest.pages.number", 1);
		chestPages = config.getInt("chest.pages.number");
	}
	
	private static List<String> convertMembersToList(Map<UUID, GuildRank> members) {
		return members.entrySet().stream().map(entry -> entry.getKey().toString() + ":" + entry.getValue().getPower()).collect(Collectors.toList());
	}
	
	private static Map<UUID, GuildRank> convertMembersFromList(List<String> stringList) {
		Map<UUID, GuildRank> members = new HashMap<>();
		for(String entry : stringList) {
			try {
			String[] parts = entry.split(":");
			UUID uuid = UUID.fromString(parts[0]);
			GuildRank rank = GuildRank.getFromPower(Integer.parseInt(parts[1]));
			if(rank != GuildRank.NOT_A_MEMBER)
				members.put(uuid, rank);
			} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
				System.err.println("Error while reading guild members. Line '"+entry+"'.");
			}
		}
		return members;
	}

	void disband() {
		String message = HalystiaRPG.PREFIX + tag + ChatColor.RED + " > " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "La guilde a été supprimée par le maître de guilde.";
		Bukkit.getOnlinePlayers().stream().filter(p -> members.containsKey(p.getUniqueId())).forEach(p -> {
			p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 10f, .7f);
			p.sendMessage(message);
		});
		super.delete();
	}

	public boolean playerJoin(Player player, String token) {
		if( ! isInviteValid(token))
			return false;
		GuildResult result = addPlayerToGuild(player);
		if(result == GuildResult.SUCCESS) {
			sendMessageToMembers(getTag() + ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + player.getName() + ChatColor.GREEN + " a rejoint la guilde.");
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vous avez bien rejoint la guilde " + getGuildName() + ChatColor.GREEN + ".");
			pendingInvite.removeIf(in -> in.getToken().equals(token));
			return true;
		}
		if(result == GuildResult.ALREADY_HERE) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous êtes déjà dans cette guilde !");
		}
		if(result == GuildResult.GUILD_FULL) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Cette guilde a atteint le nombre maximum de membres en son sein !");
		}
		return false;
	}
	
	public GuildResult playerLeaves(UUID uuid, boolean banned) {
		GuildRank rank = getPlayerRank(uuid);
		if(rank == GuildRank.MASTER)
			return GuildResult.MASTER_CANNOT_LEAVE;
		if(rank == GuildRank.NOT_A_MEMBER)
			return GuildResult.PLAYER_NOT_HERE;
		members.remove(uuid);
		saveMembers();
		OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
		Player ifIsHere = off.getPlayer();
		if(banned) {
			sendMessageToMembers(getTag() + ChatColor.RED + "Le joueur " + ChatColor.DARK_RED + off.getName() + ChatColor.RED + " a été renvoyé de la guilde.");
			if(ifIsHere != null)
				ifIsHere.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "Vous avez été renvoyé votre guilde.");
		} else {
			sendMessageToMembers(getTag() + ChatColor.RED + "Le joueur " + ChatColor.DARK_RED + off.getName() + ChatColor.RED + " a quitté la guilde.");
			if(ifIsHere != null)
				ifIsHere.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "Vous avez quitté votre guilde.");
		}
		return GuildResult.SUCCESS;
	}
	
}