package fr.jamailun.halystia.guilds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

public class GuildManager {

	private final String path;
	private final Set<Guild> guilds;
	
	public GuildManager(String path) {
		this.path = path;
		guilds = new HashSet<>();
		reload();
	}
	
	public Guild getGuild(Player player) {
		return guilds.stream().filter(g -> g.isInTheGuild(player)).findAny().orElse(null);
	}
	
	public boolean hasAGuild(Player player) {
		return getGuild(player) != null;
	}
	
	public GuildRank getRankOfPlayer(Player player) {
		Guild guild = getGuild(player);
		if(guild == null)
			return GuildRank.NOT_A_MEMBER;
		return guild.getPlayerRank(player);
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