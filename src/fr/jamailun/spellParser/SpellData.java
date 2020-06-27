package fr.jamailun.spellParser;

public class SpellData {

	private int level, mana, cooldown;
	private String name, color, id = "#none", classe = "none";

	public SpellData() {
		name = "undefined";
		color = "LIGHT_PURPLE";
	}

	public static boolean isIntKey(String key) {
		return key.equalsIgnoreCase("level") || key.equalsIgnoreCase("mana") || key.equalsIgnoreCase("cooldown");
	}

	public static boolean isStringKey(String key) {
		return key.equalsIgnoreCase("color") || key.equalsIgnoreCase("name") || key.equalsIgnoreCase("id") || key.equalsIgnoreCase("classe");
	}

	public int getLevel() {
		return level;
	}

	public int getMana() {
		return mana;
	}
	
	public String getId() {
		return id;
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean hasId() {
		return id != null && ! id.equals("#none");
	}
	
	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void associateInteger(String a, int value) {
		if(a.equalsIgnoreCase("level"))
			level = value;
		else if(a.equalsIgnoreCase("mana"))
			mana = value;
		else if(a.equalsIgnoreCase("cooldown"))
			cooldown = value;
		else
			System.err.println("Unknown integer key : (" + a + ")");
	}

	public void associateString(String a, String value) {
		if(a.equalsIgnoreCase("name"))
			name = value;
		else if(a.equalsIgnoreCase("color"))
			color = value;
		else if(a.equalsIgnoreCase("id"))
			id = value;
		else if(a.equalsIgnoreCase("classe"))
			classe = value;
		else
			System.err.println("Unknown string key : (" + a + ")");
	}

	@Override
	public String toString() {
		return "SpellData{" +
				"level=" + level +
				", mana=" + mana +
				", cooldown=" + cooldown +
				", name='" + name + '\'' +
				", color='" + color + '\'' +
				'}';
	}

	public String getClasse() {
		return classe;
	}
}