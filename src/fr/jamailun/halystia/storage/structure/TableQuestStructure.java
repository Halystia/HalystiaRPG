package fr.jamailun.halystia.storage.structure;

import static fr.jamailun.halystia.storage.structure.ColumnType.MULTI_CHAR;
import static fr.jamailun.halystia.storage.structure.ColumnType.OVER_CHAR;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.quests.Quest;

public class TableQuestStructure extends TableStructure {

	public static final ColumnTable COL_UUID = new ColumnTable("uuid", ColumnType.UUID);
	public static final ColumnTable COL_STEP = new ColumnTable("step", ColumnType.INTEGER);
	public static final ColumnTable COL_DATA = new ColumnTable("data", ColumnType.INTEGER);
	
	public TableQuestStructure(Quest quest) {
		super("quest_"+quest.getID());
		addColumn(COL_UUID, true);
		addColumn(COL_STEP);
		addColumn(COL_DATA);
	}
	
	public String getStringInsertion(Player p) {
		StringBuilder builder = new StringBuilder("INSERT INTO "+getTableName()+" (");
		for(int i = getSize(); i > 0; i--)
			builder.append(getColumns().get(i).isNulleable() ? "" : "?").append( (i > 1) ? "," : ")" );
		builder.append(" VALUES (");
		builder.append("`"+p.getUniqueId().toString()).append("`,");
		builder.append("0").append(",");	// classeID
		builder.append("0").append(",");	// classeXP
		builder.append("3").append(",");	// soulsNB
		builder.append(System.currentTimeMillis()+"").append(",");	//soulslast
		return builder.append(");").toString();
	}
	
	public String getStringUpdateColumn(Player p, ColumnTable column, Object value) {
		if( ! getColumns().contains(column))
			throw new IllegalArgumentException("Columntable is not regstered ("+column.getName()+").");
		if ( ! (value.getClass().equals(column.getType().getAssociedClass()) ))
			throw new IllegalArgumentException("Value type "+value.getClass()+" is not compatible with valuetype "+column.getType().getAssociedClass()+" of column "+column.getName());
		
		String toReplace = "UPDATE TABLE " + getTableName() + " WHERE `"+COL_UUID.getName()+"`=`"+p.getUniqueId().toString()+"` SET `"+column.getName()+"`={value};";
		
		switch (column.getType()) {
			case BOOLEAN:
				return toReplace.replace("{value}", (Boolean)value ? "0" : "1");
			case INTEGER:
			case LONG:
				return toReplace.replace("{value}", ""+(Number)value);
			case ITEM:
				ItemStack item = (ItemStack) value;
				Map<String, Object> ser = item.serialize();
				StringBuilder str = new StringBuilder();
				ser.forEach((s,o) -> {str.append(s+MULTI_CHAR+o+OVER_CHAR);});
				return toReplace.replace("{value}", str.toString());
			case LOCATION:
				Location loc = (Location) value;
				toReplace.replace("{value}", loc.getWorld().getName()+MULTI_CHAR+loc.getX()+MULTI_CHAR+loc.getY()+MULTI_CHAR+loc.getZ()+MULTI_CHAR+loc.getYaw()+MULTI_CHAR+loc.getPitch());
			case STRING_LIST:
				StringBuilder bl = new StringBuilder();
				for(Object a : ((List<?>)value)) {
					if(a instanceof String)
						bl.append((String)a).append(MULTI_CHAR);
					else
						bl.append(a.toString()).append(MULTI_CHAR);
				}
				return toReplace.replace("{value}", bl.toString());
			case STRING:
				return toReplace.replace("{value}", ((String)value));
			case UUID:
				return toReplace.replace("{value}", ((UUID)value).toString());
		}
		throw new Error("Unconsidered case ["+column.getType()+"].");
	}
	
	public String getStringGetColumn(Player p, ColumnTable column) {
		if( ! getColumns().contains(column))
			throw new IllegalArgumentException("Columntable is not regstered ("+column.getName()+").");
		return "SELECT "+column.getName()+" TABLE " + getTableName() + " WHERE `"+COL_UUID.getName()+"`=`"+p.getUniqueId().toString()+"`;";
	}
	
	public Object deserialize(String sqlValue, ColumnTable column) {
		if( ! getColumns().contains(column))
			throw new IllegalArgumentException("Columntable is not regstered ("+column.getName()+").");
		switch (column.getType()) {
		case BOOLEAN:
			if(sqlValue.isEmpty() || sqlValue.equals("0"))
				return true;
			return false;
		case INTEGER:
			return Integer.parseInt(sqlValue);
		case LONG:
			return Long.parseLong(sqlValue);
		case ITEM:
			Map<String, Object> map = new HashMap<>();
			//TODO deserialize itemstacks
			return ItemStack.deserialize(map);
		case LOCATION:
			String[] bounds = sqlValue.split(MULTI_CHAR);
			World world = Bukkit.getWorld(bounds[0]);
			double x = Double.parseDouble(bounds[1]);
			double y = Double.parseDouble(bounds[2]);
			double z = Double.parseDouble(bounds[3]);
			double yaw = Double.parseDouble(bounds[4]);
			double pitch = Double.parseDouble(bounds[5]);
			return new Location(world, x, y, z, (float) yaw, (float) pitch);
		case STRING_LIST:
			return Arrays.asList(sqlValue.split(MULTI_CHAR));
		case STRING:
			return sqlValue;
		case UUID:
			return UUID.fromString(sqlValue);
	}
	throw new Error("Unconsidered case ["+column.getType()+"].");
	}
	
}