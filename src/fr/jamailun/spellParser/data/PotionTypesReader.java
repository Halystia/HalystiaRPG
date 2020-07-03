package fr.jamailun.spellParser.data;

import java.util.Locale;

import org.bukkit.potion.PotionEffectType;

public final class PotionTypesReader {
	
	private PotionTypesReader() {}
	
	public static PotionEffectType getFromString(String string) {
		string = string.toUpperCase(Locale.ENGLISH);
		switch (string) {
			case "MOVEMENT_SPEED":
			case "SPEED":
				return PotionEffectType.SPEED;
			case "SLOW":
			case "SLOWNESS":
				return PotionEffectType.SLOW;
			case "FAST_DIGGING":
			case "HASTE":
				return PotionEffectType.FAST_DIGGING;
			case "SLOW_DIGGING":
				return PotionEffectType.SLOW_DIGGING;
			case "INCREASE_DAMAGE":
			case "STRENGTH":
			case "STRENGHT":
				return PotionEffectType.INCREASE_DAMAGE;
			case "HEAL":
			case "HEALING":
			case "INSTANT_HEAL":
			case "INSTANT_HEALTH":
				return PotionEffectType.HEAL;
			case "HARM":
			case "HARMING":
			case "DAMAGE":
			case "INSTANT_DAMAGE":
			case "INSTANT_HARM":
				return PotionEffectType.HARM;
			case "JUMP":
			case "JUMP_BOOST":
				return PotionEffectType.JUMP;
			case "CONFUSION":
			case "NAUSEA":
				return PotionEffectType.CONFUSION;
			case "REGENERATION":
			case "REGEN":
				return PotionEffectType.REGENERATION;
			case "DAMAGE_RESISTANCE":
			case "RESISTANCE":
				return PotionEffectType.DAMAGE_RESISTANCE;
			case "FIRE_RESISTANCE":
			case "LAVA_RESISTANCE":
				return PotionEffectType.FIRE_RESISTANCE;
			case "WATER_BREATHING":
				return PotionEffectType.WATER_BREATHING;
			case "INVISIBILITY":
			case "INVISIBLE":
				return PotionEffectType.INVISIBILITY;
			case "BLIND":
			case "BLINDNESS":
				return PotionEffectType.BLINDNESS;
			case "NIGHT_VISION":
				return PotionEffectType.NIGHT_VISION;
			case "HUNGER":
				return PotionEffectType.HUNGER;
			case "WEAKNESS":
				return PotionEffectType.WEAKNESS;
			case "POISON":
				return PotionEffectType.POISON;
			case "WITHER":
				return PotionEffectType.WITHER;
			case "HEALTH_BOOST":
				return PotionEffectType.HEALTH_BOOST;
			case "ABSORPTION":
				return PotionEffectType.ABSORPTION;
			case "SATURATION":
				return PotionEffectType.SATURATION;
			case "GLOWING":
				return PotionEffectType.GLOWING;
			case "LEVITATION":
				return PotionEffectType.LEVITATION;
			case "LUCK":
				return PotionEffectType.LUCK;
			case "UNLUCK":
				return PotionEffectType.UNLUCK;
			case "SLOW_FALLING":
				return PotionEffectType.SLOW_FALLING;
			case "CONDUIT_POWER":
				return PotionEffectType.CONDUIT_POWER;
			case "DOLPHINS_GRACE":
				return PotionEffectType.DOLPHINS_GRACE;
			case "BAD_OMEN":
				return PotionEffectType.BAD_OMEN;
			case "HERO_OF_THE_VILLAGE":
				return PotionEffectType.HERO_OF_THE_VILLAGE;
		}
		return null;
	}
	
}