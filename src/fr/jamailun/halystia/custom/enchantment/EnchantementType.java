package fr.jamailun.halystia.custom.enchantment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;

public enum EnchantementType {
	
	Poison,
	Glow,
	Levitation,
	Hunger,
	Frozing,	// slow + slow digging
	Cursed		// wither
	;
	
	public static EnchantementType getFromString(String str) {
		for(EnchantementType e : values())
			if(e.toString().equalsIgnoreCase(str))
				return e;
		return null;
	}
	
	private static final List<String> strings = Arrays.asList(values()).stream().map(e -> e.toString()).collect(Collectors.toList()); 
	
	public static List<String> getStrings() {
		return strings;
	}
	
	public static void effect(LivingEntity damager, LivingEntity target, List<CustomEnchantment> enchants) {
		for(CustomEnchantment enchant : enchants) {
			int level = enchant.getLevel();
			switch (enchant.getEnchantementType()) {
			case Cursed:
				target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*(4 + level), level - 1));
				target.getWorld().spawnParticle(Particle.SMOKE_NORMAL, target.getLocation().add(0,.5,0), 2);
				break;
			case Frozing:
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*(5 + level), level - 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20*(4 + level), level - 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*(4 + level), Math.min((level / 5) - 1, 3)));
				target.getWorld().spawnParticle(Particle.SNOWBALL, target.getLocation().add(0,.5,0), 50);
				break;
			case Glow:
				target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*(6 + level), level - 1));
				break;
			case Hunger:
				target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20*(4 + level), level - 1));
				break;
			case Levitation:
				target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20*(2 + level), level - 1));
				break;
			case Poison:
				target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*(4 + level), level - 1));
				break;
			default:
				HalystiaRPG.getInstance().getConsole().sendMessage("Custom encatment not configured : " + enchant.getEnchantementType());
				break;
			
			}
		}
	}
}