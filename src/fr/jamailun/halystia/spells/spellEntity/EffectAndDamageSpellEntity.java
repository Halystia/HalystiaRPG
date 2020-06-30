package fr.jamailun.halystia.spells.spellEntity;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.DamageReason;

public class EffectAndDamageSpellEntity extends SpellEntity {
	
	private final EffectData data;
	
	private final boolean hurtHimSelf, oneTarget;
	private final double range;
	
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
	private final LivingEntity launcherEntity;
	public EffectAndDamageSpellEntity(Location loc, LivingEntity launcher, int life, double range, boolean hurtHimSelf, boolean oneTarget) {
		super(loc, launcher, life);
		data = new EffectData();
		this.range = range;
		this.hurtHimSelf = hurtHimSelf;
		launcherEntity = launcher;
		this.oneTarget = oneTarget;
	}
	
	public void setFireTick(int fireTicks) {
		data.setFireTick(fireTicks);
	}
	
	public void setDamages(double damages) {
		data.setDamages(damages);
	}
	
	public void setYForce(double yForce) {
		data.setYForce(yForce);
	}
	
	public void addPotionEffect(PotionEffect effect) {
		data.addEffect(effect);
	}
	
	public void addPotionEffects(List<PotionEffect> effects) {
		effects.forEach(eff -> addPotionEffect(eff));
	}

	public void setPotionEffects(List<PotionEffect> effects) {
		data.setEffects(effects);
	}
	
	@Override
	protected void doThing() {
		for(LivingEntity en : getEntitiesAround(loc, range, hurtHimSelf)) {
			data.getEffects().forEach(e -> {
				en.removePotionEffect(e.getType());
				en.addPotionEffect(e);
			});
			if(data.getFireTick() > 0)
				en.setFireTicks(data.getFireTick());
			if(data.getDamages() > 0) {
				if(en instanceof Player) {
					HalystiaRPG.getInstance().getClasseManager().getPlayerData((Player)en).damage(data.getDamages(), launcher, DamageReason.SPELL, data.doesIgnoreArmor());
				} else {
					en.damage(data.getDamages(), launcherEntity);
				}
			}
			if(data.getyForce() > 0)
				en.setVelocity(en.getVelocity().add(new Vector(0 , data.getyForce(), 0)));
			if(oneTarget) {
				cancel();
			}
		}
	}
}
