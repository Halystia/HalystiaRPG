package fr.jamailun.halystia.guilds;

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
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.FileDataRPG;

public class Guild extends FileDataRPG {
	
	private String name, tag;
	private Map<UUID, GuildRank> members;
	private int maxMembers = 5;
	private Set<GuildInvite> pendingInvite;
	
	Guild(String path, String fileName) {
		super(path, fileName);
		members = new HashMap<>();
		loadFile();
		pendingInvite = new HashSet<>();
	}
	
	public Guild(String path, String fileName, Player creator, String name) {
		super(path, fileName);
		members = new HashMap<>();
		members.put(creator.getUniqueId(), GuildRank.MASTER);
		this.name = name;
		this.tag = name.substring(0, 2).toUpperCase();
		config.set("name", name);
		config.set("tag", tag);
		config.set("members", convertMembersToList(members));
		config.set("created", System.currentTimeMillis());
	}
	
	public String getGuildName() {
		return name;
	}
	
	public boolean isInTheGuild(Player player) {
		return members.containsKey(player.getUniqueId());
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
		if(tag.length() != 3)
			return GuildResult.WRONG_TAG_SIZE;
		//TODO test if this tag exists somewhere
		this.tag = tag;
		synchronized (config) {
			config.set("tag", tag);
			save();
		}
		return GuildResult.SUCCESS;
	}
	
	public GuildRank getPlayerRank(Player player) {
		GuildRank rank = members.get(player.getUniqueId());
		return rank == null ? GuildRank.NOT_A_MEMBER : rank;
	}
	
	public GuildResult promote(Player player) {
		if( ! isInTheGuild(player))
			return GuildResult.PLAYER_NOT_HERE;
		if( ! hasPermissions(player, GuildRank.RIGHT_ARM))
			return GuildResult.NEED_TO_BE_RA;
		GuildRank current = members.get(player.getUniqueId());
		if(current == GuildRank.MASTER)
			return GuildResult.IS_ALREADY_MASTER;
		if(current == GuildRank.CAPITAIN)
			return GuildResult.CAN_ONLY_HAVE_ONE_MASTER;
		if(current == GuildRank.CAPITAIN && guildHasRightArm())
			return GuildResult.CAN_ONLY_HAVE_RIGHT_ARM;
		members.replace(player.getUniqueId(), current.promote());
		saveMembers();
		return GuildResult.SUCCESS;
	}
	
	public GuildResult demote(Player player) {
		if( ! isInTheGuild(player))
			return GuildResult.PLAYER_NOT_HERE;
		if( ! hasPermissions(player, GuildRank.RIGHT_ARM))
			return GuildResult.NEED_TO_BE_RA;
		GuildRank current = members.get(player.getUniqueId());
		if(current == GuildRank.MASTER)
			return GuildResult.MASTER_CANNOT_BE_DEMOTE;
		if(current == GuildRank.MEMBER)
			return GuildResult.IS_ALREADY_MEMBER;
		members.replace(player.getUniqueId(), current.demote());
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
		message = getTag() + ChatColor.WHITE + source.getName() + " > " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', message);
		for(UUID uuid : members.keySet()) {
			Player to = Bukkit.getPlayer(uuid);
			if(to != null)
				to.sendMessage(message);
		}
		return GuildResult.SUCCESS;
	}
	
	public String getTag() {
		return ChatColor.GOLD + "" + ChatColor.BOLD + "[" + tag + "]";
	}
	
	public boolean hasPermissions(Player player, GuildRank minimal) {
		if( ! members.containsKey(player.getUniqueId()))
			return false;
		return members.get(player.getUniqueId()).getPower() >= minimal.getPower();
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
		if(config.contains("tag"))
			tag = config.getString("tag");
		else
			tag = name.substring(0, 2).toUpperCase();
		members = convertMembersFromList(config.getStringList("members"));
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
	
}