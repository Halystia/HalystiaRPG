package fr.jamailun.halystia.guilds;

public enum GuildRank {

	MASTER(100, "Chef"),
	RIGHT_ARM(80, "Bras droit"),
	CAPITAIN(50, "Capitaine"),
	MEMBER(10, "Membre"),
	NOT_A_MEMBER(0, "§4Erreur")
	;
	
	private final int power;
	private final String display;
	
	private GuildRank(int power, String display) {
		this.power = power;
		this.display = display;
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