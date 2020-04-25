package fr.jamailun.halystia.npcs;

import org.bukkit.configuration.ConfigurationSection;

public final class Texture {
	
	private final String texture, signature;
	
	public Texture(ConfigurationSection section) {
		texture = section.getString("texture");
		signature = section.getString("signature");
	}

	public String getTexture() {
		return texture;
	}

	public String getSignature() {
		return signature;
	}
}