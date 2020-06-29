package fr.jamailun.spellParser.structures;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.Operation;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class MathStructure extends CommandStructure {

	private final static String NB = "[0-9.]+", VAR = "%[A-Za-z0-9_]+";
	public final static String REGEX = "math (set "+VAR+" (to|=|as) "+NB+"|[A-Za-z]+ "+NB+" (to|from|of) "+VAR+")";
	
	private String variable;
	private double modifier = 0;
	private Operation op = Operation.ADD;
	
	public MathStructure(TokenContext context) {
		super(context);
	}
	
	public void read(String line, int lineNumber) {
		String[] words = line.split(" ");
		op = Operation.fromString(words[1]);
		if(op == null) {
			System.err.println("Error line " + lineNumber + " > invalid operator : '"+words[1]+".");
			invalidate();
			return;
		}
		if(op == Operation.SET) {
			variable = words[2];
			try {
				modifier = Double.parseDouble(words[4]);
			} catch ( NumberFormatException e ) {
				System.err.println("Error line " + lineNumber + " > invalid number format : '"+words[4]+".");
				invalidate();
				return;
			}
			return;
		}
		variable = words[4];
		try {
			modifier = Double.parseDouble(words[2]);
		} catch ( NumberFormatException e ) {
			System.err.println("Error line " + lineNumber + " > invalid number format : '"+words[2]+".");
			invalidate();
			return;
		}
	}

	@Override
	public void apply(ApplicativeContext context) {
		if(variable == null)
			return;
		String key = this.context.getDefinition(variable);
		if(op == Operation.SET) {
			context.define(variable, modifier);
			return;
		}
		if ( ! context.isDefinedHasNumber(key) ) {
			System.err.println("Error on spell execution : unknown variable");
			return;
		}
		context.define(variable, op.apply(context.getNumber(key), modifier));
	}

}