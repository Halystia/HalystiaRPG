package fr.jamailun.halystia.guilds.houses;

public enum HouseSize {
	UNDEFINED,
	SMALL,
	MEDIUM,
	LARGE,
	PALACE;
	
	public static HouseSize fromString(String string) {
		for(HouseSize size : values()) {
			if(size.toString().equalsIgnoreCase(string))
				return size;
		}
		return UNDEFINED;
	}
}