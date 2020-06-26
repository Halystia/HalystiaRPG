package fr.jamailun.spellParser.contexts;

import org.bukkit.entity.Entity;
import java.util.HashMap;
import java.util.Map;

public class ApplicativeContext {

	private final ApplicativeContext parent;
	private final Map<String, Entity> actives;

	public ApplicativeContext(Entity caster) {
		parent = null;
		actives = new HashMap<>();
		actives.put(TokenContext.KEY_CASTER, caster);
	}

	public ApplicativeContext(ApplicativeContext parent) {
		this.parent = parent;
		actives = new HashMap<>(parent.actives);
	}

	public void define(String variable, Entity entity) {
		actives.put(variable, entity);
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

	public Entity getEntity(String target) {
		return actives.get(target);
	}
}