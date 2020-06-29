package fr.jamailun.spellParser.contexts;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ApplicativeContext {

	private final ApplicativeContext parent;
	private final Map<String, Entity> actives;
	private final Map<String, Location> locations;
	private final Map<String, Double> numbers;

	public ApplicativeContext(Entity caster) {
		parent = null;
		actives = new HashMap<>();
		locations = new HashMap<>();
		numbers = new HashMap<>();
		actives.put(TokenContext.KEY_CASTER, caster);
	}

	public ApplicativeContext(ApplicativeContext parent) {
		this.parent = parent;
		actives = new HashMap<>(parent.actives);
		locations = new HashMap<>(parent.locations);
		numbers = new HashMap<>(parent.numbers);
	}

	public void define(String variable, Entity entity) {
		actives.put(variable, entity);
	}
	
	public void define(String variable, Location location) {
		locations.put(variable, location);
	}
	
	public void define(String variable, double number) {
		numbers.put(variable, number);
	}

	public ApplicativeContext getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public ApplicativeContext createChild() {
		return new ApplicativeContext(this);
	}

	public Entity getEntity(String key) {
		return actives.get(key);
	}
	
	public Location getLocation(String key) {
		return locations.get(key);
	}
	
	public double getNumber(String key) {
		return numbers.get(key);
	}
	
	public boolean isDefined(String key) {
		return actives.containsKey(key) || locations.containsKey(key) || numbers.containsKey(key);
	}
	
	public boolean isDefinedHasEntity(String key) {
		return actives.containsKey(key);
	}
	
	public boolean isDefinedHasLocation(String key) {
		return locations.containsKey(key);
	}
	
	public boolean isDefinedHasNumber(String key) {
		return numbers.containsKey(key);
	}
	
}