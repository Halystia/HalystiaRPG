package fr.jamailun.halystia.enemies.tags;

import org.bukkit.ChatColor;

public class MetaTag {
	
	private final String name;
	private final Type type;
	
	public MetaTag(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue(double value) {
		if(type == Type.BOOLEAN) {
			if(value > 0)
				return "true";
			return "false";
		}
		if(type == Type.STRING)
			return ""+value;
		return ""+value;
	}
	
	public String getValue(String value) {
		if(type == Type.STRING)
			return ChatColor.translateAlternateColorCodes('&', value);
		return value;
	}
	
	public boolean equals(Object o) {
		if(o instanceof MetaTag)
			return ((MetaTag)o).name.equals(name);
		return false;
	}
	
	public String toString() {
		return name;
	}
	
	public enum Type {
		BOOLEAN, DOUBLE, STRING;
	}
	
}