package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionEffect;

class EffectData {
	
	private List<PotionEffect> effects;
	private int fireTick;
	private double damages;
	private double yForce;
	private boolean ignoreArmor = false;
	
	public EffectData() {
		effects = new ArrayList<>();
		fireTick = 0;
		damages = 0;
		yForce = 0;
	}

	public List<PotionEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<PotionEffect> effects) {
		this.effects = new ArrayList<>(effects);
	}
	
	public void addEffect(PotionEffect effect) {
		effects.add(effect);
	}

	public int getFireTick() {
		return fireTick;
	}
	
	public boolean doesIgnoreArmor() {
		return ignoreArmor;
	}
	
	public void setIgnoreArmor(boolean ignoreArmor) {
		this.ignoreArmor = ignoreArmor;
	}

	public void setFireTick(int fireTick) {
		if(fireTick < 0)
			fireTick = 0;
		this.fireTick = fireTick;
	}

	public double getDamages() {
		return damages;
	}

	public void setDamages(double damages) {
		if(damages < 0)
			damages = 0;
		this.damages = damages;
	}

	public double getyForce() {
		return yForce;
	}

	public void setYForce(double yForce) {
		this.yForce = yForce;
	}
}