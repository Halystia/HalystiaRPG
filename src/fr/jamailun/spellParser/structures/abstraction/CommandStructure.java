package fr.jamailun.spellParser.structures.abstraction;

import fr.jamailun.spellParser.contexts.TokenContext;

public abstract class CommandStructure extends Structure {
	protected String target;

	public CommandStructure(TokenContext context) {
		super(context);
	}

	public void defineTarget(String target) {
		if(target == null || target.isEmpty()) {
			System.err.println("Error : target cannot be null nor empty.");
			invalidate();
			return;
		}
		this.target = target;
	}
}