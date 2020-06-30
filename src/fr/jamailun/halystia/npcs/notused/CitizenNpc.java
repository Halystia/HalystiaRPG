package fr.jamailun.halystia.npcs.notused;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.ExclamationManagement;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.NpcMode;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.npcs.Texture;
import fr.jamailun.halystia.npcs.traits.HalystiaRpgTrait;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.utils.FileDataRPG;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinLayers;
import net.citizensnpcs.trait.SkinLayers.Layer;
import net.citizensnpcs.trait.SkinTrait;

@Deprecated
public class CitizenNpc extends FileDataRPG implements RpgNpc {
	
	private NPC npc;
	private Set<Player> speackers;
	
	protected Location location;
	protected String displayName;
	
	private String questID;
	private final String id;
	private List<String> dialog;
	private Map<EquipmentSlot, ItemStack> stuff;
	private boolean exists;
	private String texture, signature;
	private NpcMode mode;
	
	private UUID lastUUID;
	
	public CitizenNpc(String path, String id, boolean shouldSpawn) {
		super(path, id);
		this.id = id;
		speackers = new HashSet<>();
		exists = false;
		dialog = new ArrayList<>();
		stuff = new HashMap<>();
		
		loadData();
		if(shouldSpawn)
			loadNPC();
	}
	
	public void spawn() {
		if(npc == null || !exists) {
			Bukkit.getLogger().log(Level.SEVERE, "NPC has not been defined !");
			return;
		}
		if( ! npc.isSpawned())
			npc.spawn(location, SpawnReason.PLUGIN);
	}
	
	public void despawn() {
		if( npc.isSpawned() )
			npc.despawn(DespawnReason.REMOVAL);
	}
	
	public void forgot() {
		CitizensAPI.getNPCRegistry().deregister(npc);
	}
	
	private void loadNPC() {
		if(npc != null) {
			npc.despawn(DespawnReason.PENDING_RESPAWN);
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
		
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, lastUUID, new Random().nextInt(1000) + 200,ChatColor.translateAlternateColorCodes('&', displayName));
		
		exists = true;
		equip();
		updateMode();
		
		spawn();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				setSkin();
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 20L);
	}
	
	public void equip() {
		Equipment equipmentTrait = npc.getTrait(Equipment.class);
		if(equipmentTrait == null) {
			System.err.println("ERREUR : equipment null pour le NPC ["+id+"].");
			return;
		}
		stuff.forEach((slot, material) -> {
			equipmentTrait.set(slot, new ItemStack(material));
		});
	}
	
	public void setEquipment(EquipmentSlot slot, ItemStack item) {
		stuff.put(slot, item);
		synchronized (file) {
			if(item.getType() == Material.AIR)
				config.set("equipment."+slot.toString(), null);
			else
				config.set("equipment."+slot.toString(), item.getType().toString());
			save();
		}
		// old : equip();
		npc.getTrait(Equipment.class).set(slot, item);;
	}
	
	public int getEntityId() {
		return npc.getEntity().getEntityId();
	}
	
	public void speak(Player p) {
		if(speackers.contains(p.getPlayer()))
			return;
		speackers.add(p.getPlayer());
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
	
	public void sendMessage(Player p, String message) {
		if( ! speackers.contains(p))
			speackers.add(p);
		message = message.replaceAll("\\%player-name\\%", p.getName());
		message = message.replaceAll("\\%npc\\%", displayName);
		message = ChatColor.translateAlternateColorCodes('&', message);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.WHITE + " > " + ChatColor.YELLOW + message);
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
		questID = config.getString("quest");
		if(questID.equals("null"))
			questID = "";
		dialog = config.getStringList("dialog");
		
		for(EquipmentSlot slot : EquipmentSlot.values()) {
			if(config.contains("equipment."+slot.toString())) {
				try {
					Material mat = Material.valueOf(config.getString("equipment."+slot.toString()));
					stuff.put(slot, new ItemStack(mat));
				} catch (IllegalArgumentException ee) {
					System.err.println("ERREUR de configuration. type inconnu : ["+config.getString("equipment."+slot.toString()+"]."));
				}
			}
		}
		
		texture = config.getString("skin.texture");
		signature = config.getString("skin.signature");
		lastUUID = UUID.fromString(config.getString("last-uuid"));
		
		try {
			mode = NpcMode.valueOf(config.getString("mode"));
		} catch (IllegalArgumentException e) {
			System.err.println("BAD MODE PARAMETER (id="+id+").");
		}
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
				config.set("name", "&6" + id);
			if( ! config.contains("skin.texture"))
				config.set("skin.texture", "eyJ0aW1lc3RhbXAiOjE1ODA0MzMyOTE2OTcsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EwNTU1Y2MyMTg1NDQwOTUwZDA2MDQ5ZGM1YTEzYWVjNDBhYTE1ZjY5MDc2NjdjYmM4ZTRkZDBiY2MyYjZjZDYifX19");
			if( ! config.contains("skin.signature"))
				config.set("skin.signature", "pb7bedHCmhmXuZ64zh5hV6MgubDQsOcIZFrRIDDM5MrcLssXqCA73dRtDDuM7dD6MUQgYVDmqUcHSEAjyiHdSIpDdNhJGP6sOL9LjYYISjISv53Cv4zqtdMEOxgzsFttuTLZxpMbuoshG+Z4DeuH7t4z4gblyhL5/6xrCw9Cwrj5S/lZg6ar5UhMP8BPAoHGY0W3L7l4zyxqqr4GN4av034tyhLpg7JFvTPDj4e5Xj7xhtezJ1hmxBaTnLqNfbVxSFtQuKyi5/zSRrToHI2wVBH5cE1VqREizTl6vGb55881EkOgs5YdKT85l7KyvyyALTLmc63aR0Xcrd7aHSpRmiJqp+F9ev5YR59bzHfY56P0dm6y6bmiVQzgFjdZFWTiOImyRP6zTmhbyZAEubUCeqEPsy5cThZdz/VBB9i9fAha9F1lIdgKtl4ETIJqy2MSSR5oeZynJJFS3g2tHbSiyoQzadpdMNf98A5+o1cUAfpI3VKiPfKT9yBxuvPtFaSk/ohnFk0PbXVheY9NWhzwa7fkFOp7dyZStCq5rWbnljalF+fPBHG+Xywa+ZLIrv4UQkYmVXAjOo++9WFji14UQDvuBX4RHST2sZcTXK/ehBYS7BSFrHYt9cD8OJZ0U3PIrzFmR52JK9J7UaFOboGGAvrISbd5T4j8I116GxKOmMs=");
			if( ! config.contains("quest"))
				config.set("quest", "null");
			if( ! config.contains("dialog"))
				config.set("dialog", new ArrayList<String>());
			if( ! config.contains("mode"))
				config.set("mode", NpcMode.STANDING.toString());
			if( ! config.contains("last-uuid"))
				config.set("last-uuid", UUID.randomUUID().toString());
			save();
		}
	}
	
	public void rename(String name) {
		displayName = name;
		synchronized (file) {
			config.set("name", name);
			save();
		}
		npc.setName(ChatColor.translateAlternateColorCodes('&', name));
	//	loadNPC();
	}
	
	public void changeLocation(Location location) {
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
		npc.teleport(location, TeleportCause.PLUGIN);
	}
	
	public void changeSkin(Texture texture) {
		this.texture = texture.getTexture();
		this.signature = texture.getSignature();
		synchronized (file) {
			config.set("skin.texture", texture.getTexture());
			config.set("skin.signature", texture.getSignature());
			save();
		}
	//	setSkin();
	}
	
	public void changeQuest(Quest quest) {
		this.questID = quest.getID();
		synchronized (file) {
			config.set("quest", questID == null ? "null" : questID);
			save();
		}
	}
	
	public void changeMode(NpcMode mode) {
		if(this.mode == mode)
			return;
		despawn();
		Class<? extends Trait> toRemove = this.mode.getTrait();
		
		if(toRemove != null)
			npc.removeTrait(toRemove);
		this.mode = mode;
		synchronized (file) {
			config.set("mode", mode.toString());
			save();
		}
		
		updateMode();
		
		spawn();
	}
	
	private void updateMode() {
		if( ! npc.hasTrait(LookClose.class)) {
			npc.addTrait(LookClose.class);
			LookClose t = npc.getTrait(LookClose.class);
			t.lookClose(true);
		}
		if( ! npc.hasTrait(HalystiaRpgTrait.class)) {
			npc.addTrait(HalystiaRpgTrait.class);
		}
		
		if(mode == NpcMode.SENTINEL) {
			npc.addTrait(mode.getTrait());
			SentinelTrait trait = (SentinelTrait) npc.getTrait(mode.getTrait());
			trait.setHealth(800);
			trait.setInvincible(false);
			new SentinelTargetLabel("monsters").addToList(trait.allTargets);
			trait.damage = 15;
			trait.respawnTime = 1;
			trait.speed = 1.5;
			trait.healRate = 5;
			trait.guardSelectionRange = 10;
			trait.needsSafeReturn = true;
			trait.spawnPoint = location.clone();
			trait.armor = 1;
			trait.drops = new ArrayList<>();
			trait.enemyDrops = true;
			trait.fightback = true;
			trait.range = 20;
		}
	}
	
	public boolean hasQuest() {
		//return questID != null && ! questID.isEmpty();
		if( ! npc.hasTrait(HalystiaRpgTrait.class))
			return false;
		return npc.getTrait(HalystiaRpgTrait.class).hasQuest();
	}
	
	public NpcMode getMode() {
		return mode;
	}
	
	public String getQuestName() {
	//	return questID;
		if( ! npc.hasTrait(HalystiaRpgTrait.class))
			return null;
		return npc.getTrait(HalystiaRpgTrait.class).getQuestName();
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
	
	public boolean equals(Object o) {
		if(o instanceof CitizenNpc)
			return ((CitizenNpc)o).id.equals(id);
		return false;
	}
	
	public String toString() {
		return "Npc[id={"+id+"}]";
	}

	public void deleteData() {
		despawn();
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

	public void free(Player player) {
		speackers.remove(player);
	}
	
	public boolean isValid() {
		return exists;
	}

	public UUID getUUID() {
		if(npc == null || !exists)
			return UUID.randomUUID();
		return npc.getUniqueId();
	}
	
	public NPC getNPC() {
		return npc;
	}
	
	private void setSkin() { // -> loadNPC and changeSkin
		//	String skinName = npc.getName();
			if(npc.isSpawned())
				npc.despawn(DespawnReason.PENDING_RESPAWN);
			/*
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, texture);
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, signature);
	       // npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
	       // npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, npc.getUniqueId().toString());
			
	        */
	        
	        /*
	        try {
	        	DataOutputStream out = null;
	            BufferedReader reader = null;
	        	URL target = new URL("https://api.mineskin.org/generate/url");
	            HttpURLConnection con = (HttpURLConnection) target.openConnection();
	            con.setRequestMethod("POST");
	            con.setDoOutput(true);
	            con.setConnectTimeout(1000);
	            con.setReadTimeout(10000);
	            out = new DataOutputStream(con.getOutputStream());
	            out.writeBytes("url=" + URLEncoder.encode("https://mineskin.org/518709860", "UTF-8"));
	            out.close();
	            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            JSONObject output = (JSONObject) new JSONParser().parse(reader);
	            JSONObject data = (JSONObject) output.get("data");
	            String uuid = (String) data.get("uuid");
	            JSONObject texture = (JSONObject) data.get("texture");
	            String textureEncoded = (String) texture.get("value");
	            String signature = (String) texture.get("signature");
	            con.disconnect();
	            
	            
	            System.out.println("uuid="+uuid);
	            
	            ((SkinnableEntity) npc.getEntity()).setSkinPersistent(UUID.randomUUID().toString(), signature, textureEncoded);
	            
	            reader.close();
	        } catch (Exception e) {
	        	
	        }*/
			npc.getTrait(SkinTrait.class).setSkinPersistent(lastUUID.toString(), signature, texture);
			SkinLayers trait = npc.getTrait(SkinLayers.class);
	        trait.setVisible(Layer.CAPE, true);
	        trait.setVisible(Layer.HAT, true);
	        trait.setVisible(Layer.JACKET, true);
	        trait.setVisible(Layer.LEFT_PANTS, true);
	        trait.setVisible(Layer.RIGHT_PANTS, true);
	        trait.setVisible(Layer.LEFT_SLEEVE, true);
	        trait.setVisible(Layer.RIGHT_SLEEVE, true);
	        
	        if(!npc.isSpawned())
	        	npc.spawn(location, SpawnReason.PLUGIN);
		}

	@Override
	public void spawn(Location location) {
		spawn();
		npc.teleport(location, TeleportCause.PLUGIN);
	}

	@Override
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	@Override
	public void setAsSpeaker(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSpeaking(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExclamationManagement getExclamation() {
		// TODO Auto-generated method stub
		return null;
	}
	
}