package fr.jamailun.spellParser.structures;

import java.util.ArrayList;
import java.util.List;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.BlockStructure;
import fr.jamailun.spellParser.structures.abstraction.CloseableStructure;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;
import fr.jamailun.spellParser.structures.abstraction.DataBlockStructure;
import fr.jamailun.spellParser.structures.abstraction.Structure;

public class GlobalStructure extends Structure {

	private List<Structure> structures;
	private List<CloseableStructure> openBlocks;

	public GlobalStructure(TokenContext context) {
		super(context);
		openBlocks = new ArrayList<>();
		structures = new ArrayList<>();
	}

	public void addData(String key, String valueString) {
		if(!isInData()) {
			System.err.println("Error : not in a data block.");
			return;
		}
		((DataBlockStructure)openBlocks.get(openBlocks.size() - 1)).registerData(key, valueString);
	}

	public void add(CommandStructure command) {
		if(openBlocks.isEmpty()) {
			structures.add(command);
			return;
		}
		CloseableStructure parent = openBlocks.get(openBlocks.size() - 1);
		if(parent instanceof BlockStructure) {
			((BlockStructure) parent).add(command);
			return;
		}
		System.err.println("Error : Could not add command to a data block.");
	}

	public void openBlock(CloseableStructure block) {
		openBlocks.add(block);
	}

	/**
	 * @return true if global structure is over.
	 */
	public boolean closeBlock() {
		if(openBlocks.isEmpty())
			return true;
		int index = openBlocks.size() - 1;
		CloseableStructure structure = openBlocks.get(index);
		structure.close();
		openBlocks.remove(index);
		structures.add(structure.getInstance());
		return openBlocks.isEmpty();
	}

	@Override
	public void apply(ApplicativeContext context) {
		structures.forEach(s -> {
			if(s.isValid()) {
				s.apply(context);
			}
		});
	}

	public boolean isFinished() {
		return openBlocks.isEmpty();
	}

	public boolean isInData() {
		if(openBlocks.isEmpty())
			return false;
		return openBlocks.get(openBlocks.size() - 1) instanceof DataBlockStructure;
	}
}