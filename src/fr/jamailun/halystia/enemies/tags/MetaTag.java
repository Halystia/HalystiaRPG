package fr.jamailun.halystia.enemies.tags;

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
		return ""+value;
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
		BOOLEAN, DOUBLE;
	}
	
}