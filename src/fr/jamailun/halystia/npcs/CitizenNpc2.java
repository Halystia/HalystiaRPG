package fr.jamailun.halystia.npcs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

import fr.jamailun.halystia.HalystiaRPG;
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
import net.citizensnpcs.trait.CurrentLocation;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinLayers;
import net.citizensnpcs.trait.SkinLayers.Layer;
import net.citizensnpcs.trait.SkinTrait;

public class CitizenNpc2 extends FileDataRPG implements RpgNpc {
	
	public final static long TIME_BETWEEN_MESSAGES = 45L;
	public final static ChatColor BASE_SPEAK_COLOR = ChatColor.YELLOW;
	
	private NPC npc;
	private Set<Player> speackers;
	
	private int citizenId;
	private final String id;
	private List<String> dialog;
	private NpcMode mode;
	
	public CitizenNpc2(String path, String id, Location shouldSpawn) {
		super(path, id);
		this.id = id;
		speackers = new HashSet<>();
		dialog = new ArrayList<>();
		
		loadData();
		
		System.out.println("Loading NPC " + id);
		if(shouldSpawn != null) {
			loadNPC(shouldSpawn);
		} else {
			npc = CitizensAPI.getNPCRegistry().getById(citizenId);
		}
	}
	
	public void updateNpc(NPC npc) {
		if( ! npc.hasTrait(HalystiaRpgTrait.class)) {
			npc.addTrait(HalystiaRpgTrait.class);
		}
		this.npc = npc;
	}
	
	public void spawn(Location location) {
		if(npc == null) {
			Bukkit.getLogger().log(Level.SEVERE, "NPC has not been defined !");
			return;
		}
		
		if( ! npc.isSpawned())
			npc.spawn(location, SpawnReason.PLUGIN);
	}
	
	public void spawn() {
		if(npc == null) {
			Bukkit.getLogger().log(Level.SEVERE, "NPC has not been defined !");
			return;
		}
		if( ! npc.isSpawned())
			npc.spawn(npc.getTrait(CurrentLocation.class).getLocation());
	}
	
	public void despawn() {
		if(npc == null)
			return;
		if( npc.isSpawned() )
			npc.despawn(DespawnReason.REMOVAL);
	}
	
	public void forgot() {
		CitizensAPI.getNPCRegistry().deregister(npc);
	}
	
	private void loadNPC(Location shouldSpawn) {
		if(npc != null) {
			npc.despawn(DespawnReason.PENDING_RESPAWN);
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
		
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, UUID.randomUUID(), citizenId, id+"_");
		
		updateMode();
		
		spawn(shouldSpawn);
	}
	
	public void setEquipment(EquipmentSlot slot, ItemStack item) {
		npc.getTrait(Equipment.class).set(slot, item);;
	}
	
	public void speak(Player p) {
		if(speackers.contains(p.getPlayer()))
			return;
		speackers.add(p.getPlayer());
		int delay = 0;
		
		for(String line : dialog) {
			if(line.startsWith("#")) {
				String[] words = line.split(" ");
				String tag = words[0].replaceFirst("#", "");
				boolean hasTag = HalystiaRPG.getInstance().getDataBase().playerHasTag(p, tag);
				
				StringBuilder builder = new StringBuilder();
				boolean first = true; // Si oui ou non on est avant le else !
				int save = -1;
				for(int i = 1; i < words.length; i++) {
					String word = words[i];
					if(word.equals("#else") && first) {
						first = false;
						if(hasTag)
							break;
						save = i + 1;
						builder = new StringBuilder();
						continue;
					}
					if(first) {
						if(!hasTag)
							continue;
						if(word.startsWith("#")) {
							action(p, word, delay);
							continue;
						}
						//tp ? give ? tag ?
						builder.append(i == 1 ? "" : " ").append(word);
					} else {
						if(word.startsWith("#")) {
							action(p, word, delay);
							continue;
						}
						builder.append(i == save ? BASE_SPEAK_COLOR : " ").append(word);
					}
					
				}
				line = builder.toString();
				
				if(save == -1 & !hasTag) {
					line = null;
				}
			}
			
			if(line != null) {
				final String fline = line;
				Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
					public void run() {
						sendMessage(p, ChatColor.translateAlternateColorCodes('&', fline));
					}
				}, delay * TIME_BETWEEN_MESSAGES);
				delay++;
			}
			
		}
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				speackers.remove(p);
			}
		}, delay * TIME_BETWEEN_MESSAGES);
	}
	
	private void action(Player p, String word, int delay) {
		if(word.startsWith("#tp")) {
			String[] coos = word.split(";");
			if(coos.length < 4) {
				Bukkit.getLogger().log(Level.WARNING, "BAD TP MENTION -> ("+word+") : not enought data.");
				return;
			}
			try {
				double x = Double.parseDouble(coos[1]);
				double y = Double.parseDouble(coos[2]);
				double z = Double.parseDouble(coos[3]);
				Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
					public void run() {
						p.teleport(new Location(npc.getEntity().getLocation().getWorld(), x, y, z));
					}
				}, delay * TIME_BETWEEN_MESSAGES);
			} catch (NumberFormatException e) {
				Bukkit.getLogger().log(Level.WARNING, "BAD TP MENTION -> ("+word+") : not numbers.");
				return;
			}
		}
	}

	public void sendMessage(Player p, String message) {
		if( ! speackers.contains(p))
			speackers.add(p);
		message = message.replaceAll("\\%player-name\\%", p.getName());
		message = message.replaceAll("\\%npc\\%", npc.getName());
		message = ChatColor.translateAlternateColorCodes('&', message);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', npc.getName()) + ChatColor.WHITE + " > " + BASE_SPEAK_COLOR + message);
	}
	
	protected void loadData() {
		preloadDefaults();
		
		dialog = config.getStringList("dialog");
		
		try {
			mode = NpcMode.valueOf(config.getString("mode"));
		} catch (IllegalArgumentException e) {
			System.err.println("BAD MODE PARAMETER (id="+id+").");
		}
		
		citizenId = config.getInt("citizen-id");
	}
	
	private void preloadDefaults() {
		synchronized (file) {
			if( ! config.contains("dialog"))
				config.set("dialog", new ArrayList<String>());
			if( ! config.contains("mode"))
				config.set("mode", NpcMode.STANDING.toString());
			if( ! config.contains("citizen-id"))
				config.set("citizen-id", (int) (new Random().nextInt(5000) + 200));
			save();
		}
	}
	
	public void rename(String name) {
		npc.setName(ChatColor.translateAlternateColorCodes('&', name));
	}
	
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', npc.getName());
	}
	
	public void changeLocation(Location location) {
		npc.teleport(location, TeleportCause.PLUGIN);
		if(npc.hasTrait(SentinelTrait.class))
			((SentinelTrait)npc.getTrait(SentinelTrait.class)).spawnPoint = location;
	}
	
	public void changeSkin(Texture texture) {
		changeSkin(texture.getTexture(), texture.getSignature());
	}
	
	public void changeQuest(Quest quest) {
		npc.getTrait(HalystiaRpgTrait.class).editQuest(quest);
	}
	
	public void changeMode(NpcMode mode) {
		if(this.mode == mode)
			return;
	//	despawn();
		Class<? extends Trait> toRemove = this.mode.getTrait();
		
		if(toRemove != null)
			npc.removeTrait(toRemove);
		this.mode = mode;
		synchronized (file) {
			config.set("mode", mode.toString());
			save();
		}
		
		updateMode();
		
	//	spawn();
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
			
			System.out.println("OK POUR SENTINEL");
			
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
			trait.spawnPoint = npc.getStoredLocation();
			trait.armor = 1;
			trait.drops = new ArrayList<>();
			trait.enemyDrops = true;
			trait.fightback = true;
			trait.range = 9;
		}
	}
	
	public boolean hasQuest() {
		if(npc == null) {
			return false;
		}
		//return questID != null && ! questID.isEmpty();
		if( ! npc.hasTrait(HalystiaRpgTrait.class)) {
			return false;
		}
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
	
	public String getConfigId() {
		return id;
	}
	
	public Location getLocation() {
		return npc.getStoredLocation();
	}
	
	public boolean isSpeacking(Player p) {
		return speackers.contains(p);
	}
	
	public boolean equals(Object o) {
		if(o instanceof CitizenNpc2)
			return ((CitizenNpc2)o).id.equals(id);
		return false;
	}
	
	public String toString() {
		return "Npc[id={"+id+"}]";
	}

	public void deleteData() {
		despawn();
		super.delete();
		npc = null;
	}

	public List<String> getDialog() {
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
		return npc != null;
	}

	public UUID getUUID() {
		if(npc == null)
			return UUID.randomUUID();
		return npc.getUniqueId();
	}
	
	public int getEntityId() {
		return citizenId;
	}
	
	public NPC getNPC() {
		return npc;
	}

	public void changeSkin(String url) {
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
			// out.writeBytes("url=" + URLEncoder.encode("https://mineskin.org/518709860",
			// "UTF-8"));
			out.writeBytes("url=" + URLEncoder.encode(url, "UTF-8"));
			out.close();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			JSONObject output = (JSONObject) new JSONParser().parse(reader);
			JSONObject data = (JSONObject) output.get("data");
			String uuid = (String) data.get("uuid");
			JSONObject texture = (JSONObject) data.get("texture");
			String textureEncoded = (String) texture.get("value");
			String signature = (String) texture.get("signature");
			con.disconnect();
			npc.getTrait(SkinTrait.class).setSkinPersistent(uuid, signature, textureEncoded);
			fixLayers();
			reader.close();
		} catch (Exception e) {
		}
	}
	
	public void changeSkin(String texture, String signature) {
		npc.getTrait(SkinTrait.class).setSkinPersistent(getUUID().toString(), signature, texture);
		fixLayers();
	}
	
	private void fixLayers() {
		SkinLayers trait = npc.getTrait(SkinLayers.class);
        trait.setVisible(Layer.CAPE, true);
        trait.setVisible(Layer.HAT, true);
        trait.setVisible(Layer.JACKET, true);
        trait.setVisible(Layer.LEFT_PANTS, true);
        trait.setVisible(Layer.RIGHT_PANTS, true);
        trait.setVisible(Layer.LEFT_SLEEVE, true);
        trait.setVisible(Layer.RIGHT_SLEEVE, true);
	}
	
}