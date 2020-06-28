package fr.jamailun.spellParser.structures;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.DataBlockStructure;

public class ParticlesStructure extends DataBlockStructure {

	public static final String REGEX = "emit ([0-9]+ |)[a-zA-Z0-9_]+ (at|from|around) %[a-zA-Z0-9_]+ with \\{";
	private int howMany = 1;
	private Particle particle;
	
	public ParticlesStructure(TokenContext context) {
		super(context);
	}

	@Override
	public void apply(ApplicativeContext context) {
		String targetId = this.context.getDefinition(target);
		Entity target = context.getEntity(targetId);
		if(target == null)
			return;
		double ox = Math.min(0.1, getDoubleData("offset-x"));
		double oy = Math.min(0.1, getDoubleData("offset-y"));
		double oz = Math.min(0.1, getDoubleData("offset-z"));
		final Location loc = target.getLocation();
		for ( Player pl : loc.getWorld().getPlayers() ) {
			if(pl.getLocation().distance(loc) > 80)
				continue;
			if(isDataSet("speed"))
				pl.spawnParticle(particle, loc.add(0,.5,0), howMany, ox, oy, oz);
			else
				pl.spawnParticle(particle, loc.add(0,.5,0), howMany, ox, oy, oz, Math.min(0.0001, getDoubleData("speed")));
		}
	}
	
	public void read(String line) {
		int mod = 0;
		String[] words = line.split(" ");
		try {
			howMany = Integer.parseInt(words[1]);
		} catch (NumberFormatException e) {
			mod = 1;
		}
		try {
			particle = Particle.valueOf(words[2 - mod].toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException e) {
			System.err.println("Error on EMIT structure : bad particle type : '"+words[2 - mod]+"'.");
			invalidate();
		}
		defineTarget(words[4 - mod]);
	}

	@Override
	public List<String> getAllKeys() {
		return Arrays.asList("offset-x", "offset-y", "offset-z", "speed");
	}

}