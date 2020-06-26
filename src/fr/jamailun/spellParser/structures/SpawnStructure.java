package fr.jamailun.spellParser.structures;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.DataBlockStructure;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SpawnStructure extends DataBlockStructure {

	public static final String REGEX = "spawn(| [0-9]+) [A-Za-z_]+ (at|around) %[A-Za-z0-9_]+ with \\{";

	private int howMany = 1;
	private EntityType type = EntityType.ZOMBIE;
	private SpawnType spawnType = SpawnType.AROUND;
	public void read(String line) {
		String[] words = line.split(" ");
		int mod = 1;
		try {
			howMany = Integer.parseInt(words[1]);
		} catch (NumberFormatException e) {
			mod = 0;
		}
		try {
			type = EntityType.valueOf(words[1 + mod].toUpperCase(Locale.ENGLISH));
		} catch(IllegalArgumentException e) {
			System.err.println("Error : unknown entity type : '"+words[1 + mod]+"'.");
			invalidate();
			return;
		}
		//It's AROUND per default. so if it's AT we change it, and ignore it if not. (already tested by regex)
		if(words[2 + mod].equals("at"))
			spawnType = SpawnType.AT;

		defineTarget(words[3 + mod]);
	}

	public SpawnStructure(TokenContext context) {
		super(context);
	}

	@Override
	public List<String> getAllKeys() {
		return Arrays.asList("health", "speed", "name", "damages", "duration");
	}

	@Override
	public void apply(ApplicativeContext applicativeContext) {
		String casterVariable = context.getDefinition(target); // = %caster
		Entity caster = applicativeContext.getEntity(casterVariable); // Player
		if(caster == null)
			return;
		for(int i = 1; i <= howMany; i++) {
			Location spawn = caster.getLocation();
			if(spawnType == SpawnType.AROUND) {
				spawn = new Location(spawn.getWorld(), spawn.getX() + (Math.random() * 2) - 1, spawn.getY() + (Math.random()), spawn.getZ() + (Math.random() * 2) - 1);
			} //TODO avec un vrai randInt x)
			final Entity invoc = Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, type);
			applyDataToEntity(invoc);
			double duration = Math.min(10, getDoubleData("duration"));
			new BukkitRunnable() {
				public void run() {
					if(invoc instanceof LivingEntity)
						((LivingEntity)invoc).damage(5000);
					else
						invoc.remove();
				}
			}.runTaskLater(null, (int)duration * 20L); //TODO API fix : JavaPlugin
		}
	}

	private void applyDataToEntity(Entity entity) {
		String name = super.getStringData("name");
		if(!name.isEmpty())
			entity.setCustomName(name);

		if( ! (entity instanceof Attributable))
			return;
		Attributable a = (Attributable) entity;
		AttributeInstance healthAttribute = a.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		double health = getDoubleData("health");
		if(healthAttribute != null && health > 0) {
			healthAttribute.setBaseValue(health);
		}

		AttributeInstance speedAttribute = a.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		double speed = getDoubleData("speed");
		if(speedAttribute != null && speed > 0) {
			speedAttribute.setBaseValue(speed);
		}

		//TODO damages avec l'API

	}

	private enum SpawnType {
		AT, AROUND
	}
}