package fr.jamailun.spellParser.structures.dataBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataList {

	private final List<DataLine<String>> dataStrings;
	private final List<DataLine<Integer>> dataInts;
	private final List<DataLine<Double>> dataDoubles;
	private final List<DataLine<Boolean>> dataBooleans;

	public DataList() {
		dataStrings = new ArrayList<>();
		dataInts = new ArrayList<>();
		dataDoubles = new ArrayList<>();
		dataBooleans = new ArrayList<>();
	}

	public Optional<String> getStringValue(String key) {
		return dataStrings.stream().filter(d -> d.getName().equals(key)).map(DataLine::getValue).findFirst();
	}

	public Optional<Integer> getIntegerValue(String key) {
		return dataInts.stream().filter(d -> d.getName().equals(key)).map(DataLine::getValue).findFirst();
	}

	public Optional<Boolean> getBooleanValue(String key) {
		return dataBooleans.stream().filter(d -> d.getName().equals(key)).map(DataLine::getValue).findFirst();
	}

	public Optional<Double> getDoubleValue(String key) {
		return dataDoubles.stream().filter(d -> d.getName().equals(key)).map(DataLine::getValue).findFirst();
	}

	public void registerString(String key, String value) {
		dataStrings.removeIf(d -> d.getName().equals(key));
		dataStrings.add(new DataLine<>(key, value));
	}

	public void registerDouble(String key, double value) {
		dataDoubles.removeIf(d -> d.getName().equals(key));
		dataDoubles.add(new DataLine<>(key, value));
	}

	public void registerInteger(String key, int value) {
		dataInts.removeIf(d -> d.getName().equals(key));
		dataInts.add(new DataLine<>(key, value));
	}

	public void registerBoolean(String key, boolean value) {
		dataBooleans.removeIf(d -> d.getName().equals(key));
		dataBooleans.add(new DataLine<>(key, value));
	}

}