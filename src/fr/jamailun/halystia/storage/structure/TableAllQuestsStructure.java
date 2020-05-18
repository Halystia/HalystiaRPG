package fr.jamailun.halystia.storage.structure;

import fr.jamailun.halystia.quests.Quest;

public class TableAllQuestsStructure extends TableStructure {

	private static final ColumnTable COL_ID = new ColumnTable("questID", ColumnType.STRING);
	
	public TableAllQuestsStructure() {
		super("allQuests");
		addColumn(COL_ID, true);
	}

	public String getStringInsertion(Quest quest) {
		return "INSERT INTO "+getTableName()+" (?) VALUES (`"+quest.getID()+"`);";
	}
	
}