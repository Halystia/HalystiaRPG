package fr.jamailun.spellParser.structures.abstraction;

import java.util.List;

import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.dataBlocks.DataList;

public abstract class DataBlockStructure extends Structure implements CloseableStructure {

	protected String target;
	private boolean open = true;
	private DataList data;

	public DataBlockStructure(TokenContext context) {
		super(context);
		data = new DataList();
	}
	
	protected boolean isDataSet(String key) {
		return data.hasDefined(key);
	}

	public void defineTarget(String target) {
		if(target == null || target.isEmpty()) {
			System.err.println("Error : target cannot be null nor empty.");
			invalidate();
			return;
		}
		this.target = target;
	}

	public void registerData(String key, String value) {
		if( ! getAllKeys().contains(key)) {
			System.err.println("Error : undefined data type : '"+key+"'.");
			return;
		}
		try {
			int valueI = Integer.parseInt(value);
			data.registerInteger(key, valueI);
			return;
		} catch (NumberFormatException ignored) {}

		try {
			double valueD = Double.parseDouble(value);
			data.registerDouble(key, valueD);
			return;
		} catch (NumberFormatException ignored) {}

		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			boolean valueB = value.equalsIgnoreCase("true");
			data.registerBoolean(key,valueB);
			return;
		}
		data.registerString(key, value);
	}

	public String getStringData(String key) {
		return data.getStringValue(key).orElse("");
	}

	@Deprecated
	public int getIntegerData(String key) {
		return data.getIntegerValue(key).orElse(0);
	}

	public double getDoubleData(String key) {
		return data.getDoubleValue(key).orElse(0.);
	}

	public boolean getBooleanData(String key) {
		return data.getBooleanValue(key).orElse(getDoubleData(key) > 0);
	}
	
	

	public abstract List<String> getAllKeys();

	@Override
	public void close() {
		open = false;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public Structure getInstance() {
		return this;
	}
}