package fr.jamailun.spellParser.structures.abstraction;

import java.util.LinkedList;
import java.util.List;

import fr.jamailun.spellParser.contexts.TokenContext;

public abstract class BlockStructure extends Structure implements CloseableStructure{

	protected List<Structure> children;
	private boolean open = true;

	public BlockStructure(TokenContext context) {
		super(context);
		children = new LinkedList<>();
	}

	public void add(Structure structure) {
		children.add(structure);
	}

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