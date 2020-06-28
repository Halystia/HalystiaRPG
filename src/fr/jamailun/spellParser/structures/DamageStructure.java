package fr.jamailun.spellParser.structures;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.DamageReason;
import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class DamageStructure extends CommandStructure {

	public static final String REGEX = "damage %[\\pL\\pN_]+ of [0-9]+";

	private int amount;

	public DamageStructure(TokenContext context) {
		super(context);
	}

	public void setDamageInt(int amount) {
		if(amount <= 0) {
			System.err.println("Error : damages must be greater than 0.");
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
			((LivingEntity)entity).damage(amount);
		if(entity instanceof Player) {
			try {
				HalystiaRPG.getInstance().getClasseManager().getPlayerData((Player)entity).damage(amount, null, DamageReason.NONE);
			} catch (NullPointerException ignored) {}
		}
	}
}