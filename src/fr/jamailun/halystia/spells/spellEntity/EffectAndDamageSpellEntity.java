package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.utils.RandomString;

public class EffectAndDamageSpellEntity extends SpellEntity {

	private final double range, damages, upperForce;
	private final List<PotionEffect> effects;
	
	private final boolean hurtHimSelf, oneTarget;
	private final int fire;
	
	/**
	 * Create a moving form emetting particles.
	 * @param loc : starting location of the spell effect
	 * @param launcher : Player who casts the spell
	 * @param life : duration of the spell effect
	 * @param effects : List of PotionEffe t to apply to LivingEntities the 
	 * @param range : range of the effects in a sphere form around the central point of the spell effect.
	 * @param hurtHimSelf : should the caster be impacted by that.
	 * @param fire : fire duration to apply to targets.
	 * @param damages : damage to apply every tick to the targets
	 * @param upperForce : force applied to the targeted entities in the positive Y direction. 
	 * @param oneTarget : if the spell should deseseapear after hit an Living Entity.
	 */
	public EffectAndDamageSpellEntity(Location loc, LivingEntity launcher, int life, List<PotionEffect> effects, double range, boolean hurtHimSelf, int fire, double damages, double upperForce, boolean oneTarget) {
		super(loc, launcher, life);
		this.effects = new ArrayList<>(effects);
		this.range = range;
		this.hurtHimSelf = hurtHimSelf;
		
		this.fire = fire;
		this.damages = damages;
		
		this.upperForce = upperForce;
		this.oneTarget = oneTarget;
	}

	@Override
	protected void doThing() {
		for(LivingEntity en : getEntitiesAround(loc, range, hurtHimSelf)) {
			for(PotionEffect effect : effects)
				en.addPotionEffect(effect);
			if(fire > 0)
				en.setFireTicks(RandomString.randInt(fire/2, fire + (fire/2)));
			if(damages > 0)
				en.damage(damages);
			if(upperForce > 0)
				en.setVelocity(en.getVelocity().add(new Vector(0, upperForce, 0)));
			if(oneTarget) {
				cancel();
			}
		}
	}
}
