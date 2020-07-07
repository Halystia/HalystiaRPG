package fr.jamailun.halystia.guilds;

import org.bukkit.ChatColor;

public enum GuildRank {

	MASTER(100, "Chef", ChatColor.GOLD),
	RIGHT_ARM(80, "Bras droit", ChatColor.DARK_GRAY),
	CAPITAIN(50, "Capitaine", ChatColor.BLUE),
	MEMBER(10, "Membre", ChatColor.GRAY),
	NOT_A_MEMBER(0, "Non membre", ChatColor.DARK_GRAY)
	;
	
	private final int power;
	private final String display;
	private final ChatColor color;
	
	private GuildRank(int power, String display, ChatColor color) {
		this.power = power;
		this.display = display;
		this.color = color;
	}
	
	public String getColor() {
		return color + "";
	}
	
	@Override
	public String toString() {
		return display;
	}
	
	public int getPower() {
		return power;
	}
	
	public static GuildRank getFromPower(int power) {
		for(GuildRank rank : values())
			if(rank.power == power)
				return rank;
		return NOT_A_MEMBER;
	}
	
	public GuildRank promote() {
		switch (this) {
		case MASTER:
			return MASTER;
		case RIGHT_ARM:
			return RIGHT_ARM;
		case CAPITAIN:
			return RIGHT_ARM;
		case MEMBER:
			return CAPITAIN;
		case NOT_A_MEMBER:
			return NOT_A_MEMBER;
		}
		return NOT_A_MEMBER;
	}
	
	public GuildRank demote() {
		switch (this) {
		case MASTER:
			return MASTER;
		case RIGHT_ARM:
		return CAPITAIN;
		case CAPITAIN:
			return MEMBER;
		case MEMBER:
			return MEMBER;
		case NOT_A_MEMBER:
			return NOT_A_MEMBER;
		}
		return NOT_A_MEMBER;
	}
	
}