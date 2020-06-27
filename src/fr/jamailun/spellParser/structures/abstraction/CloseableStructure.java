package fr.jamailun.spellParser.structures.abstraction;

public interface CloseableStructure {

	void close();

	boolean isOpen();
	
	Structure getInstance();

}