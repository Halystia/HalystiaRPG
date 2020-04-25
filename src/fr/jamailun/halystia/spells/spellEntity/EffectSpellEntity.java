package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class EffectSpellEntity extends SpellEntity {

	private final double range;
	private final List<PotionEffect> effects;
	
	private final boolean hurtHimSelf;
	
	public EffectSpellEntity(Location loc, Player launcher, int life, List<PotionEffect> effects, double range, boolean hurtHimSelf) {
		super(loc, launcher, life);
		this.effects = new ArrayList<>(effects);
		this.range = range;
		this.hurtHimSelf = hurtHimSelf;
	}
	
	@Override
	protected void doThing() {
		for(LivingEntity e : getEntityAround(loc, range, hurtHimSelf)) {
			for(PotionEffect effect : effects)
				((LivingEntity)e).addPotionEffect(effect);
		}
	}
}
