package fr.jamailun.halystia.guilds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class GuildManager {

	public static final int GUILD_NAME_LENGTH_MIN = 3, GUILD_NAME_LENGTH_MAX = 20;
	
	private final String path;
	private final Set<Guild> guilds;
	
	public GuildManager(String path) {
		this.path = path;
		File dir = new File(path);
		if( ! dir.exists() )
			dir.mkdirs();
		guilds = new HashSet<>();
		reload();
	}
	
	public Guild getGuild(Player player) {
		return guilds.stream().filter(g -> g.isInTheGuild(player)).findAny().orElse(null);
	}
	
	public Guild getGuild(UUID uuid) {
		return guilds.stream().filter(g -> g.isInTheGuild(uuid)).findAny().orElse(null);
	}
	
	public boolean hasAGuild(Player player) {
		return getGuild(player) != null;
	}
	
	/**
	 * Check if two player can Fight each other
	 * @param a first player's UUID
	 * @param b second player's UUID
	 * @return true if players are in the same Guild AND that guild deny pvp
	 */
	public boolean areInTheSameNoPvpGroup(UUID a, UUID b) {
		Guild ga = getGuild(a);
		Guild gb = getGuild(b);
		if(ga == null || gb == null)
			return false;
		if(ga != gb)
			return false;
		return ! ga.allowsPVP();
	}
	
	public GuildRank getRankOfPlayer(Player player) {
		Guild guild = getGuild(player);
		if(guild == null)
			return GuildRank.NOT_A_MEMBER;
		return guild.getPlayerRank(player);
	}
	
	public boolean joinGuild(Player player, String token) {
		if ( getGuild(player) != null) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Tu possèdes déjà une guilde : impossible d'en rejoindre une autre !");
			return false;
		}
		for(Guild guild : guilds) {
			if(guild.isInviteValid(token)) {
				return guild.playerJoin(player, token);
			}
		}
		player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce token d'invitation n'existe pas !");
		return false;
	}
	
	boolean tagExists(String tag) {
		return guilds.stream().anyMatch(g -> g.getPureTag().equalsIgnoreCase(tag));
	}
	
	public Guild createGuild(Player player, String guildName) {
		if ( guildName.length() < GUILD_NAME_LENGTH_MIN) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le nom de la guilde doit faire au moins "+GUILD_NAME_LENGTH_MIN+" caractères !");
			return null;
		}
		if ( guildName.length() > GUILD_NAME_LENGTH_MAX) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le nom de la guilde doit faire moins de "+GUILD_NAME_LENGTH_MAX+" caractères !");
			return null;
		}
		if ( hasAGuild(player) ) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Tu possèdes déjà une guilde : impossible d'en créer une nouvelle !");
			return null;
		}
		for(Guild gld : guilds) {
			if ( gld.getGuildName().equalsIgnoreCase(guildName)) {
				player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce nom de guilde existe déjà !");
				return null;
			}
		}
		Guild guild = new Guild(path, guildName.toLowerCase(Locale.ENGLISH).replaceAll(" ", "_"), player, guildName);
		guilds.add(guild);
		return guild;
	}
	
	public void disbandGuild(Player player, Guild guild) {
		if ( guild.getPlayerRank(player) != GuildRank.MASTER ) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Impossible de disband une guilde dont tu n'es pas le dirigeant !");
			return;
		}
		guild.disband();
		guilds.remove(guild);
	}
	
	public void reload() {
		guilds.clear();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				guilds.add(new Guild(path, name));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}