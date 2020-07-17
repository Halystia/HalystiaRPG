package fr.jamailun.halystia.guilds;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

public class GuildMemberData {

	private final static String SEP_CHAR = ":";
	
	private UUID uuid;
	private GuildRank rank;
	private int xpPercent, xpGiven;
	private long joinedDate;
	
	public GuildMemberData(Player p, GuildRank rank) {
		uuid = p.getUniqueId();
		this.rank = rank;
		xpPercent = 5;
		xpGiven = 0;
		joinedDate = System.currentTimeMillis();
	}
	
	public GuildMemberData(String serialized) {
		String[] data = serialized.split(SEP_CHAR);
		try {
			uuid = UUID.fromString(data[0]);
			int power = Integer.parseInt(data[1]);
			rank = GuildRank.getFromPower(power);
			xpGiven = Integer.parseInt(data[2]);
			xpPercent = Integer.parseInt(data[3]);
			joinedDate = Long.parseLong(data[4]);
		} catch (IndexOutOfBoundsException ignored) {}
	}
	
	public GuildRank getRank() {
		return rank;
	}
	
	public void setRank(GuildRank rank) {
		if(rank != GuildRank.NOT_A_MEMBER)
			this.rank = rank;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public int getExpGiven() {
		return xpGiven;
	}
	
	public void addExpGiven(int exp) {
		if(exp < 0)
			return;
		this.xpGiven += exp;
	}
	
	public double getExpPercent() {
		return ((double)xpPercent) / 100.0;
	}
	
	public int getExpPercentInt() {
		return xpPercent;
	}
	
	public Date getJoinedDate() {
		return new Date(joinedDate);
	}
	
	public void changeExpPercent(int percent) {
		if(percent < 0)
			percent = 0;
		if(percent > 100)
			percent = 100;
		this.xpPercent = percent;
	}
	
	public String serialize() {
		return uuid.toString()
				+ SEP_CHAR + rank.getPower()
				+ SEP_CHAR + xpGiven
				+ SEP_CHAR + xpPercent
				+ SEP_CHAR + joinedDate;
	}
	
}