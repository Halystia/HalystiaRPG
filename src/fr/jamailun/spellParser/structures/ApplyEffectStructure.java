package fr.jamailun.spellParser.structures;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.PotionTypesReader;
import fr.jamailun.spellParser.data.TimeUnit;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class ApplyEffectStructure extends CommandStructure {

	public static final String REGEX = "apply [\\pL\\pN_]+ [0-9]+ during [0-9]+ (seconds|ticks|second|tick) to %[\\pL\\pN_]+";

	private PotionEffectType type = PotionEffectType.SPEED;
	private int force = 0, duration = 0;

	public ApplyEffectStructure(TokenContext context) {
		super(context);
	}

	public void setEffectString(String effect) {
		type = PotionTypesReader.getFromString(effect);
		if(type == null) {
			System.err.println("Error : Effect name '"+effect+"' does not exist.");
			invalidate();
		}
	}

	public void setForceInt(int force) {
		if(force < 0 || force > 255) {
			System.err.println("Error : force must be between 0 and 255.");
			invalidate();
			return;
		}
		this.force = force - 1;
	}

	public void setDurationInt(int duration, String unit) {
		if (duration < 0) {
			System.err.println("Error : duration must be greater or equal to 0.");
			invalidate();
			return;
		}
		if (duration == 0) {
			this.duration = 1;
			return;
		}
		TimeUnit timeUnit = TimeUnit.fromString(unit);
		if(timeUnit == null) {
			invalidate();
			System.err.println("Error : unknown time unit : '"+unit+"'");
			return;
		}
		this.duration = duration * timeUnit.getTicksDuration();
	}

	@Override
	public void apply(ApplicativeContext context) {
		Entity entity = context.getEntity(target);
		if(entity == null)
			return;
		if( ! (entity instanceof LivingEntity) )
			return;
		((LivingEntity)entity).addPotionEffect(new PotionEffect(type, duration, force));
	}

}