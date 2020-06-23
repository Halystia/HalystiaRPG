package fr.jamailun.halystia.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.WorldServer;

public class PlayerUtils {
	private List<Player> players;

	public static double getMaxHealthOfPlayer(Player p) {
		double base = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double equip = 0;
		equip += getHealthOfItem(p.getInventory().getHelmet(), EquipmentSlot.HEAD);
		equip += getHealthOfItem(p.getInventory().getChestplate(), EquipmentSlot.CHEST);
		equip += getHealthOfItem(p.getInventory().getLeggings(), EquipmentSlot.LEGS);
		equip += getHealthOfItem(p.getInventory().getBoots(), EquipmentSlot.FEET);
		equip += getHealthOfItem(p.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
		equip += getHealthOfItem(p.getInventory().getItemInOffHand(), EquipmentSlot.OFF_HAND);
		double all = base + equip;
		if(all < 1)
			all = 1;
		return all;
	}

	public static double getHealthOfItem(ItemStack item, EquipmentSlot slot) {
		double value = 0;
		if(item != null)
			if(item.hasItemMeta())
				if(item.getItemMeta().hasAttributeModifiers())
					if(item.getItemMeta().getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH) != null)
						value += item.getItemMeta().getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH).stream().filter(mod -> mod.getSlot() == slot).mapToDouble(modif -> modif.getAmount()).sum();
		return value;
	}

	public PlayerUtils(Player p) {
		players = new ArrayList<Player>();
		players.add(p);
	}

	public PlayerUtils(List<Player> players) {
		this.players = players;
	}

	public PlayerUtils setNameTag(String nameTag) throws Exception {
		for (Player p : players) {
			Field name = EntityHuman.class.getDeclaredField("name");
			name.setAccessible(true);
			for (Player po : Bukkit.getOnlinePlayers())
				if (po.getUniqueId() != p.getUniqueId())
					((CraftPlayer)po).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)p).getHandle()));
		}
		return this;
	}

	public PlayerUtils sendTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		for(Player p : players)
			sendTitle(p, Integer.valueOf(fadeIn), Integer.valueOf(stay), Integer.valueOf(fadeOut), title, subtitle);
		return this;
	}

	public PlayerUtils sendTabTitle(String header, String footer) {
		for(Player p : players)
			sendTabTitle(p, header, footer);
		return this;
	}

	public PlayerUtils clearTitle() {
		for(Player p : players)
			clearTitle(p);
		return this;
	}

	public PlayerUtils playSound(Sound sound, Location location) {
		for(Player p : players)
			p.playSound(location, sound, 1.0F, 1.0F);
		return this;
	}

	public PlayerUtils playSound(Sound sound) {
		for(Player p : players)
			p.playSound(p.getLocation(), sound, 1.0F, 1.0F);
		return this;
	}

	public PlayerUtils setTabListName(String name) {
		for(Player p : players)
			p.setPlayerListName(name);
		return this;
	}

	public PlayerUtils sendActionBar(String message) {
		for (Player p : players) {
			IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
			PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(ppoc);
		}
		return this;
	}

	private void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		try {
			if (title != null) {
				title = ChatColor.translateAlternateColorCodes('&', title);
				title = title.replaceAll("%player%", player.getDisplayName());

				Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
				Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
				Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				Object titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle, fadeIn, stay, fadeOut });
				sendPacket(player, titlePacket);

				e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
				chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent") });
				titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle });
				sendPacket(player, titlePacket);
			}

			if (subtitle != null) {
				subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
				subtitle = subtitle.replaceAll("%player%", player.getDisplayName());

				Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
				Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
				Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);

				e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
				chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void clearTitle(Player player) {
		sendTitle(player, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), "", "");
	}

	private void sendTabTitle(Player player, String header, String footer) {
		if (header == null)
			header = "";
		header = ChatColor.translateAlternateColorCodes('&', header);

		if (footer == null)
			footer = "";
		footer = ChatColor.translateAlternateColorCodes('&', footer);

		header = header.replaceAll("%player%", player.getDisplayName());
		footer = footer.replaceAll("%player%", player.getDisplayName());
		try {
			Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + header + "\"}" });
			Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
			Object packet = titleConstructor.newInstance(new Object[0]);
			Field aField = packet.getClass().getDeclaredField("a");
			aField.setAccessible(true);
			aField.set(packet, tabHeader);
			Field bField = packet.getClass().getDeclaredField("b");
			bField.setAccessible(true);
			bField.set(packet, tabFooter);
			sendPacket(player, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void sendDamageMarker(double damages, Location loc)  {
		WorldServer s = ((CraftWorld)players.get(0).getWorld()).getHandle();
		EntityArmorStand stand = new EntityArmorStand(s, loc.getX(), loc.getY(), loc.getZ());
		stand.setNoGravity(true);
		PacketPlayOutSpawnEntityLiving packetCreate = new PacketPlayOutSpawnEntityLiving((EntityLiving) stand);

		for(Player p : players) {
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetCreate);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				stand.setArms(true);
				stand.setBasePlate(false);
				stand.setInvisible(true);
				stand.setInvulnerable(true);
				stand.setMarker(true);
				stand.setCustomNameVisible(true);
				NumberFormat formatter = new DecimalFormat("##.#");
				if(damages >= 100 && damages < 1000)
					formatter = new DecimalFormat("###.#");
				else 
					formatter = new DecimalFormat("####.#");
				stand.setCustomName(new ChatMessage(ChatColor.RED+""+ChatColor.BOLD+formatter.format(damages)));
				PacketPlayOutEntityMetadata packetData = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
				players.forEach(p -> ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetData));
			}
		}.runTask(HalystiaRPG.getInstance());

		new BukkitRunnable() {
			@Override
			public void run() {
				PacketPlayOutEntityDestroy packetDestroy = new PacketPlayOutEntityDestroy(stand.getId());
				players.forEach(p -> ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetDestroy));
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 30L);
	}
}