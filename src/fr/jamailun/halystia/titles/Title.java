package fr.jamailun.halystia.titles;

import org.bukkit.ChatColor;

public final class Title implements Cloneable {
	
	private final String tag, displayName;
	
	public Title(String tag, String displayName) {
		this.tag = tag;
		this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public String getTag() {
		return tag;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public boolean equals(Object o) {
		if ( o instanceof Title )
			return ((Title)o).tag.equals(tag);
		return false;
	}
	
	public String toString() {
		return "Title["+tag+"]";
	}
	
	public Title clone() {
		return new Title(tag, displayName);
	}
}