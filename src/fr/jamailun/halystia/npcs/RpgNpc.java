package fr.jamailun.halystia.npcs;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.quests.Quest;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public interface RpgNpc extends Dialogable {

	public void spawn(Location location);
	public void spawn();
	public void despawn();
	
	public void speak(Player p);
	public void sendMessage(Player p, String message);
	public void free(Player p);
	public void setAsSpeaker(Player p);
	public boolean isSpeaking(Player p);
	
	public void rename(String name);
	public String getDisplayName();
	
	public void changeSkin(Texture texture);
	
	public void changeQuest(Quest quest);
	public boolean hasQuest();
	public String getQuestName();
	
	public void changeMode(NpcMode mode);
	public NpcMode getMode();

	public void setEquipment(EquipmentSlot slot, ItemStack item);
	
	public Location getLocation();
	public void changeLocation(Location location);
	public boolean isSpeacking(Player p);
	
	public void deleteData();
	public String getConfigId();
	public boolean isValid();
	public UUID getUUID();
	public NPC getNPC();
	public int getEntityId();
	
}