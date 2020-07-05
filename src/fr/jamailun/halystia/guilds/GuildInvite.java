package fr.jamailun.halystia.guilds;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.jamailun.halystia.utils.RandomString;

public final class GuildInvite {

	private final Guild guild;
	private final String token;
	private final UUID from;
	
	GuildInvite(Player from, Guild guild) {
		this.from = from.getUniqueId();
		this.guild = guild;
		token = new RandomString(12).nextString();
	}

	public Guild getGuild() {
		return guild;
	}

	public String getToken() {
		return token;
	}

	public UUID getFrom() {
		return from;
	}
	
}