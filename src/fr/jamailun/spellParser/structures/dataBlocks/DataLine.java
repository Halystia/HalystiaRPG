package fr.jamailun.spellParser.structures.dataBlocks;

public class DataLine<T> {

	private final String name;
	private final T value;

	public DataLine(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}
}