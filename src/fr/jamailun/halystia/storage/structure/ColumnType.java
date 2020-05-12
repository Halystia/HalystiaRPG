package fr.jamailun.halystia.storage.structure;

public enum ColumnType {
	STRING_LIST("TEXT", java.util.List.class),
	UUID("TEXT", java.util.UUID.class),
	STRING("TEXT", String.class),
	INTEGER("INT(11)", Integer.class),
	LOCATION("TEXT", org.bukkit.Location.class),
	ITEM("TEXT", org.bukkit.inventory.ItemStack.class),
	BOOLEAN("BOOL", Boolean.class),
	LONG("BIGINT", Long.class)
	;
	
	public final static String MULTI_CHAR = ";";
	public final static String OVER_CHAR = "//";
	
	private final String sqlType;
	private final Class<?> associedClass;
	
	private ColumnType(String sqlType, Class<?> associedClass) {
		this.sqlType = sqlType;
		this.associedClass = associedClass;
	}
	
	public String getSQLType() {
		return sqlType;
	}
	
	public Class<?> getAssociedClass() {
		return associedClass;
	}
}