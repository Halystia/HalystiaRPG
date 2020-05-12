package fr.jamailun.halystia.storage.structure;

public class ColumnTable {
	private final String name;
	private final ColumnType type;
	private final boolean nulleable;
	public ColumnTable(String name, ColumnType type) {
		this(name, type, false);
	}
	public ColumnTable(String name, ColumnType type, boolean nulleable) {
		this.name = name;
		this.type = type;
		this.nulleable = nulleable;
	}
	public boolean isNulleable() {
		return nulleable;
	}
	public String getName() {
		return name;
	}
	public ColumnType getType() {
		return type;
	}
	public String getColumnCreation() {
		return "`"+name+"` " + type.getSQLType() + " " + (nulleable ? "" : " NOT NULL");
	}
	public boolean equals(Object o) {
		if(o instanceof ColumnTable)
			return ((ColumnTable)o).getName().equals(name);
		return false;
	}
}