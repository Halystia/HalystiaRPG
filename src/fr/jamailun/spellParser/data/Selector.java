package fr.jamailun.spellParser.data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public enum Selector {
	
	/**
	 * Always returns true, for all entities.
	 */
	ALL(Entity.class, "all", "everything"),
	
	/**
	 * Returns true for all living entities.
	 */
	ENTITY(LivingEntity.class, "entity", "entities", "living"),
	
	/**
	 * Returns true for all monsters.
	 */
	MOB(Monster.class, "mob", "mobs", "monster", "monsters", "creature", "creatures"),
	
	/**
	 * Returns true for Player entites (also get NPC).
	 */
	PLAYER(Player.class, "player", "players"),
	
	/**
	 * Used for internal usage. Always returns false.
	 */
	NONE(null);
	
	private final List<String> words;
	private final Class<? extends Entity> clazz;
	
	private Selector(Class<? extends Entity> clazz, String... strings) {
		this.clazz = clazz;
		this.words = Arrays.asList(strings);
	}
	
	public static Selector fromString(String word) {
		if(word == null)
			return Selector.NONE;
		word = word.toLowerCase(Locale.ENGLISH);
		for(Selector sl : values()) {
			if(sl.words.contains(word))
				return sl;
		}
		return Selector.NONE;
	}
	
	public boolean isAllowed(Entity entity) {
		if(this == NONE)
			return false;
		return clazz.isInstance(entity);
	}
}