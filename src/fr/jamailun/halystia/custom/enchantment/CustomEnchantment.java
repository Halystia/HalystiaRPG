package fr.jamailun.halystia.custom.enchantment;

public class CustomEnchantment {
	
	private final int level;
	private final EnchantementType type;
	
	public CustomEnchantment(EnchantementType type, int level) {
		this.type = type;
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public EnchantementType getEnchantementType() {
		return type;
	}
}