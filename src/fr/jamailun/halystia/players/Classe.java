package fr.jamailun.halystia.players;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.Locale;

import org.bukkit.ChatColor;

/**
 * The classe of a Player in the RPG.
 * @author jamailun
 * @see {@link fr.jamailun.halystia.players.PlayerData PlayerData}.
 */
public enum Classe {
	
	NONE(0, GRAY + "(Aucune)", RED+"error", RED+"error", RED+"error"),
	
	ALCHIMISTE(1, "Alchimiste", "Prêtre", "Sage", "Arcaniste"),
	EPEISTE(2, "Épéiste", "Soldat", "Guerrier", "Héros"),
	ARCHER(3, "Archer", "Ranger", "Tireur d'élite", "Sniper"),
	INVOCATEUR(4, "Invocateur", "Chaman", "Druide", "Archimage");
	
	private final String displayName, rank2, rank3, rank4;
	private final int id;
	
	public static Classe fromString(String classe) {
		classe = classe.toLowerCase(Locale.FRANCE);
		switch(classe) {
		case "alchimiste":
		case "alchi":
			return ALCHIMISTE;
		case "épéiste":
		case "epeiste":
			return EPEISTE;
		case "archer":
			return ARCHER;
		case "invocateur":
		case "invoc":
			return INVOCATEUR;
		}
		return NONE;
	}
	
	
	private Classe(int id, String displayName, String rank2, String rank3, String rank4) {
		this.id = id;
		this.displayName = displayName;
		this.rank2 = rank2;
		this.rank3 = rank3;
		this.rank4 = rank4;
	}
	
	public String getDisplayName(int level) {
		return GREEN + getTitlename(level);
	}
	
	public String getName() {
		return displayName;
	}
	
	public String getTitlename(int level) {
		String title = getName();
		if(level >= 30)
			title = rank2;
		if(level >= 60)
			title = rank3;
		if(level > 90)
			title = rank4;
		return title;
	}
	
	public int getClasseId() {
		return id;
	}
	
	public static Classe getClasseWithId(int id) {
		for(Classe c : values())
			if(c.id == id)
				return c;
		return NONE;
	}
	
	public static ChatColor getColor(int level) {
		if(level < 10)
			return ChatColor.GRAY;
		if(level < 30)
			return ChatColor.YELLOW;
		if(level < 60)
			return ChatColor.BLUE;
		if(level < 90)
			return ChatColor.GREEN;
		return ChatColor.LIGHT_PURPLE;
	}
	
}
