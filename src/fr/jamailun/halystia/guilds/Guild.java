package fr.jamailun.halystia.guilds;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.Levelable;
import fr.jamailun.halystia.utils.MenuGUI;

public class Guild extends FileDataRPG implements Levelable {
	
	public static final int[] TAG_LENGHT = {3, 4};
	
	private String name, tag;
	private List<GuildMemberData> members;
	//private Map<UUID, GuildRank> members;
	private int maxMembers = 5;
	private Set<GuildInvite> pendingInvite = new HashSet<>();
	private boolean pvp = false;
	
	private final GuildChest chest;
	private int powerToPutItems = 1, powerToGetItems = 50;
	private int chestPages = 1;
	private int level = 1, xp = 0;
	
	public Guild(String path, String fileName) {
		super(path, fileName);
		members = new ArrayList<>();
		loadFile();
		
		chest = new GuildChest(this, config.getConfigurationSection("chest.pages"));
	}
	
	public Guild(String path, String fileName, Player creator, String name) {
		super(path, fileName);
		members = new ArrayList<>();
		members.add(new GuildMemberData(creator, GuildRank.MASTER));
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
		return (int) Math.max(1, 0.5 * Math.pow(Math.max(0, exp), 0.12));
	}
	
	@Override
	public int getExpForLevel(int level) {
		return (int) Math.exp( Math.log( 2 * Math.min(20, Math.max(0, level))) / 0.12 );
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
		return members.stream().map(data -> data.getUUID()).map(id -> Bukkit.getOfflinePlayer(id).getName()).collect(Collectors.toList());
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
		for(GuildMemberData data : members) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(data.getUUID());
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
		return isInTheGuild(player.getUniqueId());
	}
	
	public boolean isInTheGuild(UUID uuid) {
		return getData(uuid) != null;
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
		members.add( new GuildMemberData(player, GuildRank.MEMBER) );
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
		return getPlayerRank(player.getUniqueId());
	}
	
	public GuildRank getPlayerRank(UUID uuid) {
		GuildMemberData data = getData(uuid);
		return data == null ? GuildRank.NOT_A_MEMBER : data.getRank();
	}
	
	public GuildResult promote(UUID uuid) {
		GuildMemberData data = getData(uuid);
		if( data == null)
			return GuildResult.PLAYER_NOT_HERE;
		GuildRank current = data.getRank();
		if(current == GuildRank.MASTER)
			return GuildResult.IS_ALREADY_MASTER;
		if(current == GuildRank.RIGHT_ARM)
			return GuildResult.CAN_ONLY_HAVE_ONE_MASTER;
		if(current == GuildRank.CAPITAIN && guildHasRightArm())
			return GuildResult.CAN_ONLY_HAVE_RIGHT_ARM;
		data.setRank(current.promote());
		saveMembers();
		return GuildResult.SUCCESS;
	}
	
	private GuildMemberData getData(UUID uuid) {
		return members.stream().filter(data -> data.getUUID().equals(uuid)).findFirst().orElse(null);
	}
	
	public GuildResult demote(UUID uuid) {
		GuildMemberData data = getData(uuid);
		if( data == null)
			return GuildResult.PLAYER_NOT_HERE;
		GuildRank current = data.getRank();
		if(current == GuildRank.MASTER)
			return GuildResult.MASTER_CANNOT_BE_DEMOTE;
		if(current == GuildRank.MEMBER)
			return GuildResult.IS_ALREADY_MEMBER;
		data.setRank(current.demote());
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
		forAllMembers(p -> p.sendMessage(message));
	}
	
	public String getTag() {
		return ChatColor.GRAY + "[" + ChatColor.WHITE + tag + ChatColor.GRAY + "] " + ChatColor.WHITE;
	}
	
	public boolean hasPermissions(Player player, GuildRank minimal) {
		return members.stream().filter(data -> data.getUUID().equals(player.getUniqueId())).anyMatch(data -> data.getRank().getPower() >= minimal.getPower());
	}
	
	public List<String> getMembersDisplay() {
		List<String> list = new ArrayList<>();
		members.stream().forEach(data -> {
			String name = Bukkit.getOfflinePlayer(data.getUUID()).getName();
			GuildRank rank = data.getRank();
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
		return members.stream().anyMatch(data -> data.getRank() == GuildRank.RIGHT_ARM);
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
	
	private static List<String> convertMembersToList(List<GuildMemberData> members) {
		return members.stream().map(data -> data.serialize()).collect(Collectors.toList());
	}
	
	private static List<GuildMemberData> convertMembersFromList(List<String> stringList) {
		List<GuildMemberData> members = new ArrayList<>();
		for(String entry : stringList) {
			GuildMemberData data = new GuildMemberData(entry);
			if(data.getRank() != GuildRank.NOT_A_MEMBER)
				members.add(data);
		}
		return members;
	}

	void disband() {
		String message = HalystiaRPG.PREFIX + tag + ChatColor.RED + " > " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "La guilde a été supprimée par le maître de guilde.";
		forAllMembers(p -> {
			p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 10f, .7f);
			p.sendMessage(message);
		});
		super.delete();
	}
	
	public void forAllMembers(Consumer<Player> action) {
		for(GuildMemberData data : members) {
			Player pl = Bukkit.getPlayer(data.getUUID());
			if(pl != null)
				action.accept(pl);
		}
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
		members.removeIf(data -> data.getUUID().equals(uuid));
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

	public double getExpPercentOfPlayer(UUID uniqueId) {
		try {
			return members.stream().filter(data -> data.getUUID().equals(uniqueId)).findAny().get().getExpPercent();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}

	public String generatePercentBar() {
		if(level == 20)
			return GOLD + "Niveau max !";
		double percent = getPercentXp();
		
		StringBuilder builder = new StringBuilder(DARK_GRAY+"[");
		for(int i = 1; i <= 15; i++) {
			double currentPercent = ((double)i) / ((double)15);
			if(currentPercent <= percent)
				builder.append(GREEN+PlayerData.BAR_CHAR);
			else
				builder.append(GRAY+PlayerData.BAR_CHAR);
		}
		builder.append(DARK_GRAY+"]");
		return builder.toString();
	}
	
	public double getPercentXp() {
		int level = getLevel();
		double lvlN0 = level == 1 ? 0 : getExpForLevel(level);
		double lvlN1 = getExpForLevel(level + 1);
		double filled = getExpAmount() - lvlN0;
		double upper = lvlN1 - lvlN0;
		double percent = filled / upper;
		return percent;
	}
	
}