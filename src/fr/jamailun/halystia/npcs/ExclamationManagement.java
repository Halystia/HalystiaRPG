package fr.jamailun.halystia.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.WorldServer;

//belongs to 1 NPC
public class ExclamationManagement {

	private final Map<UUID, ExclamationServerSide> recipes = new HashMap<>();
	private final RpgNpc npc;

	public ExclamationManagement(RpgNpc npc) {
		this.npc = npc;
	}

	public void changeState(Player player, ExclamationType ex) {
		final UUID uuid = player.getUniqueId();
		if(recipes.containsKey(uuid)) {
			recipes.get(uuid).changeStatus(ex, player);
			return;
		}
		recipes.put(uuid, new ExclamationServerSide(npc.getLocation()));
	}
	
	public void npcMoved() {
		try {
			npc.getLocation();
		} catch(Exception e) {
			return;
		}
		recipes.forEach((uid, exc) -> {
			exc.move(npc.getLocation());
		});
	}

	public static enum ExclamationType {
		NONE(Material.AIR),						// rien
		QUEST_POSSIBLE(Material.LIME_WOOL),		// vert : apparait quand la quete est dispo
		QUEST_REPORT(Material.BLUE_WOOL),		// bleu : quand un NPC attend un rapport.
		NOT_LEVEL(Material.RED_WOOL); 			// rouge : quand la quete existe mais niveau insuffisant.
		private ExclamationType(Material item) {
			this.material = item;
		}
		private Material material;
	}
	
	public void purge() {
		recipes.forEach((uid, exc) -> {
			exc.delete();
		});
		recipes.clear();
	}

	private static interface Exclamation {
		public void changeStatus(ExclamationType type, Player recipe);
		public void move(Location loc, Player recipe);
		public void delete(Player recipe);
		public void rotate(Player recipe);
	}
	
	private final static class ExclamationServerSide {
		private final static double Y_DELTA = 1.7;
		private final ArmorStand standServer;
		private final EntityArmorStand standClient;
		private Location loc;
		
		public ExclamationServerSide(Location npcLoc) {
			loc = npcLoc.add( 0, Y_DELTA, 0 );
			standServer = loc.getWorld().spawn(loc, ArmorStand.class);
			
			standServer.setGravity(false);
			standServer.setArms(true);
			standServer.setBasePlate(false);
			standServer.setVisible(false);
			standServer.setInvulnerable(true);
			standServer.setMarker(true);
			standServer.setSmall(true);
			standServer.setCustomName("-exclamation");
			standServer.setCustomNameVisible(false);
			
			standClient = ((CraftArmorStand) standServer).getHandle();
		}
		
		public void changeStatus(ExclamationType type, Player recipe) {
			if(recipe == null)
				return;
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(standClient.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(type.material)));
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}
		
		public void move(Location loc) {
			standServer.teleport( loc );
		}

		public void delete() {
			standServer.remove();
		}
		
		public void delete(Player recipe) {
			if(recipe == null)
				return;
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(standClient.getId());
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}

		private float rot = 0;
		private double h = 0;
		private boolean up = true;
		public void rotate(Player recipe) {
			if(recipe == null)
				return;
			rot = (rot + 6) % 360;
			h += 0.005 * (up ? 1 : -1);
			if(h > 0.06 || h < -0.06)
				up = ! up;
			standServer.teleport( new Location(loc.getWorld(), loc.getX(),  loc.getY() + h,  loc.getZ(), rot, 0) );
			standClient.setLocation(loc.getX(), loc.getY() + h, loc.getZ(), rot, 0);
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(standClient);
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	@SuppressWarnings("unused")
	private final static class ExclamationClientSide implements Exclamation {

		private final static double Y_DELTA = 1.7;
		private ExclamationType type;
		private final EntityArmorStand stand;
		private Location loc;

		public ExclamationClientSide(Player recipe, ExclamationType type, Location npcLoc) {
			WorldServer s = ((CraftWorld)recipe.getWorld()).getHandle();
			stand = new EntityArmorStand(s, npcLoc.getX(), npcLoc.getY() + Y_DELTA, npcLoc.getZ());
			loc = new Location(recipe.getWorld(), npcLoc.getX(), npcLoc.getY() + Y_DELTA, npcLoc.getZ());
			stand.setNoGravity(true);
			
			//stand.setRightArmPose(new Vector3f(50f, 00f, 20f));
			
			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving((EntityLiving) stand);
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
			
			//PacketPlayOutAttachEntity
			
			changeStatus(type, recipe);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					stand.setArms(true);
					stand.setBasePlate(false);
					stand.setInvisible(true);
					stand.setInvulnerable(true);
					stand.setMarker(true);
					stand.setSmall(true);
					stand.setCustomName(new ChatMessage("-"));
					stand.setCustomNameVisible(false);
					PacketPlayOutEntityMetadata packetData2 = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
					((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packetData2);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), 2L);
			
		}
		
		public void changeStatus(ExclamationType type, Player recipe) {
			if(recipe == null)
				return;
	//		System.out.println("changement vraiment effectué ! ("+type+":"+type.material+").");
			this.type = type;
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(type.material)));
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}
		
		public void move(Location loc, Player recipe) {
			if(recipe == null)
				return;
			loc = new Location(recipe.getWorld(), loc.getX(), loc.getY() + Y_DELTA, loc.getZ());
			stand.setLocation(loc.getX(), loc.getY() + Y_DELTA, loc.getZ(), 0, 0);
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(stand);
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}

		public void delete(Player recipe) {
			if(recipe == null)
				return;
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(stand.getId());
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}

		private float rot = 0;
		private double h = 0;
		private boolean up = true;
		public void rotate(Player recipe) {
			if(recipe == null)
				return;
			rot = (rot + 6) % 360;
			h += 0.005 * (up ? 1 : -1);
			if(h > 0.06 || h < -0.06)
				up = ! up;
			stand.setLocation(loc.getX(), loc.getY() + h, loc.getZ(), rot, 0);
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(stand);
			((CraftPlayer)recipe).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public void purge(Player player) {
		if(player == null || recipes.get(player.getUniqueId()) == null)
			return;
		recipes.get(player.getUniqueId()).delete(player);
		recipes.remove(player.getUniqueId());
	}
	
	public static void cleanAtBegining(World world) {
		world.getEntities().forEach(e -> {
			if(e instanceof ArmorStand && e.getCustomName() != null) {
				if((e.getCustomName().equals("-exclamation")||e.getCustomName().equals("-"))) {
					e.remove();
				}
			}
		});
	}
	
	public void purgeWhenServerClose() {
		recipes.values().forEach(ExclamationServerSide::delete);
		recipes.clear();
	}

	public void rotate() {
		recipes.entrySet().stream().forEach(entry -> {
			entry.getValue().rotate(Bukkit.getPlayer(entry.getKey()));
		});
	}
}