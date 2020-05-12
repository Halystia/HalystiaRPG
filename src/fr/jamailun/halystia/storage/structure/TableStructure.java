package fr.jamailun.halystia.storage.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

public abstract class TableStructure {

	private final String name;
	private final List<ColumnTable> columns;
	private Optional<ColumnTable> primary;
	
	public TableStructure(String name) {
		this.name = name;
		columns = new ArrayList<>();
		primary = Optional.empty();
	}
	
	protected boolean addColumn(ColumnTable column) {
		return addColumn(column, false);
	}
	
	protected boolean addColumn(ColumnTable column, boolean isPrimaryKey) {
		Validate.notNull(column, "Column cannot be null.");
		if(columns.contains(column))
			return false;
		if(isPrimaryKey) {
			if(primary.isPresent())
				return false;
			primary = Optional.ofNullable(column);
		}
		columns.add(column);
		return true;
	}
	
	public final String getTableName() {
		return name;
	}
	
	public final List<ColumnTable> getColumns() {
		return new ArrayList<>(columns);
	}
	
	public final int getSize() {
		return columns.size();
	}
	
	public final String getTableCreationString() {
		if( ! primary.isPresent() || columns.isEmpty()) {
			Bukkit.getLogger().severe("No primary column detected OR no columns.");
			return "error";
		}
		StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS "+name+" (");
		for(ColumnTable column : columns)
			builder.append(column.getColumnCreation() + ", ");
		builder.append("PRIMARY KEY (`"+primary.get()+"`)");
		return builder.append(");").toString();
	}
	
	public boolean equals(Object o) {
		if(o instanceof TableStructure)
			return ((TableStructure)o).getTableName().equals(name);
		return false;
	}
	
}