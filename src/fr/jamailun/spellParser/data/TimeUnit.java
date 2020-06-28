package fr.jamailun.spellParser.data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum TimeUnit {

	TICK(1, "tick", "ticks"),
	SECOND(20, "second", "seconds"),
	MINUTE(1200, "minutes", "minutes");
	
	private final int ticks;
	private final List<String> words;
	
	private TimeUnit(int tickEquivalence, String...strings) {
		this.ticks = tickEquivalence;
		this.words = Arrays.asList(strings);
	}
	
	public int getTicksDuration() {
		return ticks;
	}
	
	public static TimeUnit fromString(String string) {
		if(string == null)
			return null;
		string = string.toLowerCase(Locale.ENGLISH);
		for(TimeUnit unit : values())
			if(unit.words.contains(string))
				return unit;
		return null;
	}
}