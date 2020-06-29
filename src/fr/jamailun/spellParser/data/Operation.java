package fr.jamailun.spellParser.data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Operation {

	SET("set"),
	ADD("add"),
	REM("rem", "remove", "sub", "substract"),
	MUL("mul", "mult", "multiply"),
	DIV("div", "divide"),
	MOD("mod", "modulo"),
	;
	
	private final List<String> words;
	
	private Operation(String...strings) {
		this.words = Arrays.asList(strings);
	}
	
	/**
	 * Apply the operation to a number and an other.
	 * <br/>FOr example, with Operation.MUL, this will return source * otherNumber;
	 * @param source first number to apply the operation
	 * @param otherNumber the argument of the operation
	 * @return the new number.
	 */
	public double apply(double source, double otherNumber) {
		switch (this) {
		case ADD:
			return source + otherNumber;
		case DIV:
			return source / otherNumber;
		case MOD:
			return source % otherNumber;
		case MUL:
			return source * otherNumber;
		case REM:
			return source - otherNumber;
		case SET:
			return source;
		}
		return source;
	}
	
	public static Operation fromString(String string) {
		if(string == null)
			return null;
		string = string.toLowerCase(Locale.ENGLISH);
		for(Operation ob : values())
			if(ob.words.contains(string))
				return ob;
		return null;
	}
}