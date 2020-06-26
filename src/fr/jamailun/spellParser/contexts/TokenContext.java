package fr.jamailun.spellParser.contexts;

import java.util.HashMap;
import java.util.Map;

public class TokenContext {

	public final static String KEY_CASTER = "%caster";

	private final TokenContext parent;
	private final Map<String, String> definitions;

	public TokenContext() {
		parent = null;
		definitions = new HashMap<>();
	}

	public TokenContext(TokenContext parent) {
		definitions = new HashMap<>(parent.definitions);
		this.parent = parent;
	}

	public void define(String toDefine, String definition) {
		if(definitions.containsKey(toDefine))
			System.err.println("Error : key '"+toDefine+"' is already defined. Errors will occur.");
		definitions.put(toDefine, definition);
	}

	public boolean isDefined(String key) {
		return definitions.containsKey(key);
	}

	public String getDefinition(String key) {
		if( ! definitions.containsKey(key))
			return key;
		return definitions.get(key);
	}

	public TokenContext getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public TokenContext createChild() {
		return new TokenContext(this);
	}
}