package fr.jamailun.halystia.jobs.system;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class HeadCreator {
	public static ItemStack createHead(String uuid, String texture){
		GameProfile profile = createGameProfile(texture, UUID.fromString(uuid));
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		if(!set(headMetaClass, headMeta, "profile", profile, "Unable to inject profile")) {
			return null;
		}
		head.setItemMeta(headMeta);
		SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
		head.setItemMeta(skullMeta);
		return head;
	}

	private static GameProfile createGameProfile(String texture, UUID id){
		GameProfile profile = new GameProfile(id, null);
		PropertyMap propertyMap = profile.getProperties();
		if(propertyMap == null){
			Bukkit.getLogger().log(Level.WARNING, "No property map found in GameProfile, can't continue.");
			return null;
		}
		propertyMap.put("textures", new Property("textures", texture));
		propertyMap.put("Signature", new Property("Signature", "1234"));
		return profile;
	}

	private static boolean set(Class<?> sourceClass, Object instance, String fieldName, Object value, String message){
		try{
			Field field = sourceClass.getDeclaredField(fieldName);
			boolean accessible = field.isAccessible();
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			int modifiers = modifiersField.getModifiers();
			boolean isFinal = (modifiers & 0x10) == 16;
			if (!accessible) {
				field.setAccessible(true);
			}
			if (isFinal){
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, modifiers & 0xFFFFFFEF);
			}
			try{
				field.set(instance, value);
			}
			finally
			{
				if (isFinal) {
					modifiersField.setInt(field, modifiers | 0x10);
				}
				if (!accessible) {
					field.setAccessible(false);
				}
			}
			return true;
		}
		catch (IllegalArgumentException ex){
			Bukkit.getLogger().log(Level.SEVERE, message + ": unsupported version.");
		}
		catch (IllegalAccessException ex){
			Bukkit.getLogger().log(Level.SEVERE, message + ": security exception.");
		}
		catch (NoSuchFieldException ex){
			Bukkit.getLogger().log(Level.SEVERE, message + ": unsupported version, field " + fieldName + " not found.");
		}
		catch (SecurityException ex){
			Bukkit.getLogger().log(Level.SEVERE, message + ": security exception.");
		}
		return false;
	}
}