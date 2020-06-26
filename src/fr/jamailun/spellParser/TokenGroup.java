package fr.jamailun.spellParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenGroup {

	private List<String> possibilities;

	public TokenGroup() {
		possibilities = new ArrayList<>();
	}

	public TokenGroup(String... tokens) {
		possibilities = Arrays.asList(tokens);
	}

	public void add(String possibility) {
		possibilities.add(possibility);
	}

	public boolean matches(String string) {
		return possibilities.stream().anyMatch(s -> s.equalsIgnoreCase(string));
	}

	public String getRegexValue() {
		StringBuilder builder = new StringBuilder();
		possibilities.forEach(p -> builder.append(p).append("|"));
		return builder.toString().substring(0, builder.length() - 1);
	}
}