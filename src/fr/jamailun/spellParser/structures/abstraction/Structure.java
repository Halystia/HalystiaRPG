package fr.jamailun.spellParser.structures.abstraction;

import fr.jamailun.spellParser.contexts.TokenContext;

public abstract class Structure implements Structural {

	private boolean valid = true;
	protected final TokenContext context;

	public Structure(TokenContext context) {
		this.context = context;
	}

	protected void invalidate() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}

}