package fr.jamailun.halystia.npcs.notused;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.Texture;
import fr.jamailun.halystia.npcs.notused.NmsNpc.NPCAnimation;
import fr.jamailun.halystia.utils.FileDataRPG;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

@Deprecated
public class Npc extends FileDataRPG {
	
	protected Set<Player> viewers;
	private Set<Player> speackers;
	private int entityId;
	
	protected Location location;
	protected String displayName;
	//protected String uuid;
	
	private EntityPlayer npc;
	private String questID;
	private final String id;
	private List<String> dialog;
	private GameProfile gp;
	private Map<EnumItemSlot, ItemStack> equipment;
	
	private String texture, signature;
	
	public Npc(String path, String id) {
		super(path, id);
		this.id = id;
		viewers = new HashSet<>();
		speackers = new HashSet<>();
		exists = false;
		dialog = new ArrayList<>();
		equipment = new HashMap<>();
		
		loadData();
		loadNpc();
	}
	
	public void reload() {
		depopToAllPlayers();
		loadNpc();
		showToAllPlayers();
	}
	
	public void showToAllPlayers() {
		//Bukkit.getOnlinePlayers().stream().filter(p -> HalystiaRPG.isInRpgWorld(p)).forEach(p -> showPlayer(p));
		NPC n = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.translateAlternateColorCodes('&', displayName));
		n.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, texture);
		n.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, signature);
		n.spawn(location);
	}
	
	@Deprecated
	public void showPlayer(Player p) {
		if(viewers.contains(p))
			return;
		
		
		/*
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		connection.sendPacket(new PacketPlayOutEntityLook(entityId, (byte) ((int) (this.location.getYaw() * 256.0F / 360.0F)), (byte) 0, true));
		viewers.add(p);
		
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gp, 0, EnumGamemode.NOT_SET, CraftChatMessage.fromString(ChatColor.translateAlternateColorCodes('&', displayName))[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getField(packet, "b");
		players.add(data);
		this.setField(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		this.setField(packet, "b", players);*/
		new BukkitRunnable() {
			@Override
			public void run() {
				//sendPacket(packet, p);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				equipment.forEach((slot, item) -> {
					setEquipment(slot, item);
				});
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 40L);
	}
	
	public void depopToAllPlayers() {
		Bukkit.getOnlinePlayers().stream().filter(p -> HalystiaRPG.isInRpgWorld(p)).forEach(p -> depop(p));
	}
	
	public void depop(Player p) {
		if( ! viewers.contains(p))
			return;
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		viewers.remove(p);
	}
	
	protected void loadNpc() {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		gp = new GameProfile(UUID.nameUUIDFromBytes(id.getBytes()), ChatColor.translateAlternateColorCodes('&', displayName));
		setSkin(texture, signature);
		WorldServer world = ((CraftWorld) Bukkit.getWorld(HalystiaRPG.WORLD)).getHandle();
		npc = new EntityPlayer(server, world, gp, new PlayerInteractManager(world));
		npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entityId = npc.getId();
		exists = true;
		clockOfPosition();
	}
	
	protected void loadData() {
		preloadDefaults();
		ConfigurationSection sectionLocation = config.getConfigurationSection("location");
		String worldName = sectionLocation.getString("world");
		double x = sectionLocation.getDouble("x");
		double y = sectionLocation.getDouble("y");
		double z = sectionLocation.getDouble("z");
		double yaw = sectionLocation.getDouble("yaw");
		
		location = new Location(Bukkit.getWorld(worldName), x, y, z, (float)yaw, 0);
		displayName = config.getString("name");
		//uuid = config.getString("texture");
		questID =config.getString("quest");
		dialog = config.getStringList("dialog");
		
		for(EnumItemSlot slot : EnumItemSlot.values()) {
			if(config.contains("equipment."+slot.toString())) {
				try {
					Material mat = Material.valueOf(config.getString("equipment."+slot.toString()));
					equipment.put(slot, new ItemStack(mat));
				} catch (IllegalArgumentException ee) {
					System.err.println("ERREUR de configuration. type inconnu : ["+config.getString("equipment."+slot.toString()+"]."));
				}
			}
		}
		
		if(config.contains("skin.texture") && config.contains("skin.signature")) {
			texture = config.getString("skin.texture");
			signature = config.getString("skin.signature"); 
		}
		
	}
	
	public void rename(String name) {
		displayName = name;
		synchronized (file) {
			config.set("name", name);
			save();
		}
		loadNpc();
	}
	
	public void move(Location location) {
		this.location = location.clone();
		synchronized (file) {
			ConfigurationSection sectionLocation = config.getConfigurationSection("location");
			sectionLocation.set("world", location.getWorld().getName());
			sectionLocation.set("x", location.getX());
			sectionLocation.set("y", location.getY());
			sectionLocation.set("z", location.getZ());
			sectionLocation.set("yaw", location.getYaw());
			save();
		}
		loadNpc();
	}
	
	public void rotateHead(float pitch, float yaw) {
		PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.entityId, getFixRotation(yaw), (byte) pitch, true);
		PacketPlayOutEntityHeadRotation packet_1 = new PacketPlayOutEntityHeadRotation();
		this.setField(packet_1, "a", this.entityId);
		this.setField(packet_1, "b", getFixRotation(yaw));
		this.sendPacket(packet);
		this.sendPacket(packet_1);
	}
	
	private byte getFixRotation(float yawpitch) {
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}
	
	public void changeSkin(Texture texture) {
		depopToAllPlayers();
		synchronized (file) {
			config.set("skin.texture", texture.getTexture());
			config.set("skin.signature", texture.getSignature());
			save();
		}
		loadData();
		loadNpc();
		showToAllPlayers();
	}
	
	public void changeQuest(String questID) {
		this.questID = questID;
		synchronized (file) {
			config.set("quest", questID == null ? "null" : questID);
			save();
		}
	}
	
	/*
	 *  set the texture and signature in the gameprofile, to submit it you must reload player.
	 */
	private void setSkin(String texture, String signature) {
		if(gp.getProperties() == null)
			System.err.println("properties nulles !");
		gp.getProperties().put("textures", new Property("textures", texture, signature));
	}
	
	private void preloadDefaults() {
		synchronized (file) {
			if( ! config.contains("location.world"))
				config.set("location.world", HalystiaRPG.WORLD);
			if( ! config.contains("location.x"))
				config.set("location.x", 0);
			if( ! config.contains("location.y"))
				config.set("location.y", 0);
			if( ! config.contains("location.z"))
				config.set("location.z", 0);
			if( ! config.contains("location.yaw"))
				config.set("location.yaw", 0);
			if( ! config.contains("name"))
				config.set("name", "&a" + id);
			if( ! config.contains("skin.texture"))
				config.set("skin.texture", "eyJ0aW1lc3RhbXAiOjE1ODA0MzMyOTE2OTcsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EwNTU1Y2MyMTg1NDQwOTUwZDA2MDQ5ZGM1YTEzYWVjNDBhYTE1ZjY5MDc2NjdjYmM4ZTRkZDBiY2MyYjZjZDYifX19");
			if( ! config.contains("skin.signature"))
				config.set("skin.signature", "pb7bedHCmhmXuZ64zh5hV6MgubDQsOcIZFrRIDDM5MrcLssXqCA73dRtDDuM7dD6MUQgYVDmqUcHSEAjyiHdSIpDdNhJGP6sOL9LjYYISjISv53Cv4zqtdMEOxgzsFttuTLZxpMbuoshG+Z4DeuH7t4z4gblyhL5/6xrCw9Cwrj5S/lZg6ar5UhMP8BPAoHGY0W3L7l4zyxqqr4GN4av034tyhLpg7JFvTPDj4e5Xj7xhtezJ1hmxBaTnLqNfbVxSFtQuKyi5/zSRrToHI2wVBH5cE1VqREizTl6vGb55881EkOgs5YdKT85l7KyvyyALTLmc63aR0Xcrd7aHSpRmiJqp+F9ev5YR59bzHfY56P0dm6y6bmiVQzgFjdZFWTiOImyRP6zTmhbyZAEubUCeqEPsy5cThZdz/VBB9i9fAha9F1lIdgKtl4ETIJqy2MSSR5oeZynJJFS3g2tHbSiyoQzadpdMNf98A5+o1cUAfpI3VKiPfKT9yBxuvPtFaSk/ohnFk0PbXVheY9NWhzwa7fkFOp7dyZStCq5rWbnljalF+fPBHG+Xywa+ZLIrv4UQkYmVXAjOo++9WFji14UQDvuBX4RHST2sZcTXK/ehBYS7BSFrHYt9cD8OJZ0U3PIrzFmR52JK9J7UaFOboGGAvrISbd5T4j8I116GxKOmMs=");
			if( ! config.contains("quest"))
				config.set("quest", "null");
			if( ! config.contains("dialog"))
				config.set("dialog", new ArrayList<String>());
			save();
		}
	}
	
	public void rightClick(Player p) {
		if(speackers.contains(p.getPlayer()))
			return;
		speackers.add(p.getPlayer());
		System.out.println("clic");
		int delay = 0;
		for(String line : dialog) {
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
				public void run() {
					sendMessage(p, ChatColor.translateAlternateColorCodes('&', line));
				}
			}, delay * NpcManager.TIME_BETWEEN_MESSAGES);
			delay++;
		}
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				speackers.remove(p);
			}
		}, delay * NpcManager.TIME_BETWEEN_MESSAGES);
	}
	
	public void free(Player p) {
		speackers.remove(p);
	}
	
	public void sendMessage(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET + " > " + msg);
	}
	
	public boolean hasQuest() {
		return questID != null && !questID.isEmpty();
	}
	
	public String getQuestName() {
		return questID;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public String getName() {
		return displayName;
	}
	
	public String getConfigId() {
		return id;
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public boolean isSpeacking(Player p) {
		return speackers.contains(p);
	}
	
	public boolean canView(Player p) {
		return viewers.contains(p);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Npc)
			return ((Npc)o).entityId == entityId;
		return false;
	}
	
	public String toString() {
		return "Npc[id={"+id+"},uuid="+entityId+"]";
	}

	public void deleteData() {
		viewers.forEach(p -> depop(p));
		super.delete();
		exists = false;
	}

	public List<String> getDialog() {
		if(dialog.isEmpty())
			return Arrays.asList("&eIl fait beau aujourd'hui non ?");
		return new ArrayList<>(dialog);
	}
	
	public void clearDialog() {
		dialog.clear();
		saveDialog();
	}
	
	public boolean setDialogLine(String text, int line) {
		if(line < 0 || line >= dialog.size())
			return false;
		dialog.set(line, text);
		saveDialog();
		return true;
	}

	public void addDialogLine(String text) {
		dialog.add(text);
		saveDialog();
	}
	
	public boolean removeDialogLine(int line) {
		if(line < 0 || line >= dialog.size())
			return false;
		dialog.remove(line);
		saveDialog();
		return true;
	}
	
	public boolean insertDialogLine(String string, int line) {
		if(line < 0 || line >= dialog.size())
			return false;
		boolean passed = false;
		ArrayList<String> nd = new ArrayList<>(dialog);
		nd.add("temp");
		for(int i = 0; i < dialog.size(); i++) {
			if(i == line) {
				passed = true;
				nd.set(i, string);
				continue;
			}
			if(passed)
				nd.set(i, dialog.get(i-1));
			else
				nd.set(i, dialog.get(i));
		}
		dialog = nd;
		return true;
	}
	
	private void saveDialog() {
		synchronized (file) {
			config.set("dialog", dialog);
			save();
		}
	}

	public GameProfile getGameProfile() {
		return gp;
	}
	
	@SuppressWarnings("unused")
	private Object getField(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setEquipment(EnumItemSlot slot, ItemStack item) {
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
		this.setField(packet, "a", this.entityId);
		this.setField(packet, "b", slot);
		this.setField(packet, "c", CraftItemStack.asNMSCopy(item));
		this.sendPacket(packet);
		synchronized (file) {
			if(item.getType() == Material.AIR)
				config.set("equipment."+slot.toString(), null);
			else
				config.set("equipment."+slot.toString(), item.getType().toString());
			save();
		}
	}
	
	private void setField(Object obj, String field_name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(field_name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	private void sendPacket(Packet<?> packet) {
		for (Player p : viewers) {
			this.sendPacket(packet, p);
		}
	}
	
	/*
	 * set npc animation such as Swing arm ect. I recommend using method 'public void setAnimation(NPCAnimation animation)'
	 */
	private void setAnimation(byte animation) {
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
		this.setField(packet, "a", this.entityId);
		this.setField(packet, "b", animation);
		this.sendPacket(packet);
	}

	/*
	 * set npc animation such as Swing arm ect.
	 */
	public void setAnimation(NPCAnimation animation) {
		setAnimation((byte) animation.getId());
	}

	public void attack(Player p) {
		p.playSound(location, Sound.ENTITY_BLAZE_HURT, 1f, .5f);
		setAnimation(NPCAnimation.TAKE_DAMAGE);
		moveForward(5);
	}
	
	private void clockOfPosition() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if( ! isValid()) {
					cancel();
					return;
				}
				if(viewers.isEmpty())
					return;
				Player closest = null;
				double dist = 4;
				for(Player pl : viewers) {
					double distance = pl.getLocation().distance(location);
					if (distance < dist) {
						closest = pl;
						dist = distance;
					}
				}
				if(closest == null)
					rotateHead(0, location.getYaw());
				else
					rotateToPlayer(closest);
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 15L, 15L);
	}
	
	private boolean exists;
	public boolean isValid() {
		return exists;
	}
	
	public void rotateToPlayer(Player p) {
		Vector look = p.getLocation().toVector().subtract(location.toVector());
		Location toLook = look.toLocation(location.getWorld()).setDirection(look);
		rotateHead(toLook.getPitch(), toLook.getYaw());
	}
	
	public void moveForward(double distance) {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			@Override
			public void run() {
			}
		}, 10L);
		
	}

}