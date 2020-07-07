package fr.jamailun.halystia.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
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

	private final Map<UUID, Exclamation> recipes;
	private final RpgNpc npc;

	public ExclamationManagement(RpgNpc npc) {
		this.npc = npc;
		recipes = new HashMap<>();
	}

	public void changeState(Player player, ExclamationType ex) {
		final UUID uuid = player.getUniqueId();
		if(recipes.containsKey(uuid)) {
			if(recipes.get(uuid).type == ex) {
	//			System.out.println("veut passer à "+ex+" : c'est déjà le cas !");
				return;
			}
	//		System.out.println("passage de "+recipes.get(uuid).type+" vers -> " + ex);
			recipes.get(uuid).changeStatus(ex, player);
			return;
		}
	//	System.out.println("on se met en " + ex);
		recipes.put(uuid, new Exclamation(player, ex, npc.getLocation()));
	}
	
	public void npcMoved() {
		try {
			npc.getLocation();
		} catch(Exception e) {
			return;
		}
		recipes.forEach((uid, exc) -> {
			exc.move(npc.getLocation(), Bukkit.getPlayer(uid));
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
			exc.delete(Bukkit.getPlayer(uid));
		});
		recipes.clear();
	}

	private final static class Exclamation {

		private final static double Y_DELTA = 1.7;
		private ExclamationType type;
		private final EntityArmorStand stand;
		private Location loc;

		public Exclamation(Player recipe, ExclamationType type, Location npcLoc) {
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
		recipes.get(player.getUniqueId()).delete(player);
	}

	public void rotate() {
		recipes.entrySet().stream().forEach(entry -> {
			entry.getValue().rotate(Bukkit.getPlayer(entry.getKey()));
		});
	}
}