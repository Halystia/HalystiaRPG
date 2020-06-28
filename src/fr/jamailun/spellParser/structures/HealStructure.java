package fr.jamailun.spellParser.structures;

import java.util.Objects;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class HealStructure extends CommandStructure {

	public static final String REGEX = "heal %[\\pL\\pN_]+ of [0-9]+";

	private int amount;

	public HealStructure(TokenContext context) {
		super(context);
	}

	public void setHealInt(int amount) {
		if(amount <= 0) {
			System.err.println("Error : health value must be greater than 0.");
			invalidate();
			return;
		}
		this.amount = amount;
	}

	@Override
	public void apply(ApplicativeContext context) {
		Entity entity = context.getEntity(target);
		if(entity == null)
			return;
		if( entity instanceof LivingEntity)
			((LivingEntity)entity).setHealth(Math.max(Objects.requireNonNull(((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue(), ((LivingEntity)entity).getHealth() + amount));
	}
}