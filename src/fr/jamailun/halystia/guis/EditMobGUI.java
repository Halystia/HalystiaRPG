package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.commands.CommandEditMobs;
import fr.jamailun.halystia.constants.Equipment;
import fr.jamailun.halystia.constants.GUIEntityType;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.Loop;
import fr.jamailun.halystia.utils.MenuGUI;
import fr.jamailun.halystia.utils.RandomString;

public class EditMobGUI extends MenuGUI {

	private final Player p;
	private boolean closeToSetName = false;
	private boolean canhaveArmor = true;
	private Loop<GUIEntityType> entity;
	private Map<MetaTag, Double> metadata;
	
	private double[] tauxDrops;
	private ItemStack[] drops;
	private int xp;
	private String name;
	
	private final DecimalFormat decimalFormat;
	
	private final static String NULL_NAME = DARK_GRAY + "[Aucun]";
	public static HashMap<Player, EditMobGUI> editors = new HashMap<>();
	public static HashMap<Player, EditMobGUI> editorsMD = new HashMap<>();
	
	public EditMobGUI(Player p) {
		this(p, null);
	}
	
	public EditMobGUI(Player p, String key) {
		super(DARK_BLUE+"Créer un nouveau mob", 9*6, HalystiaRPG.getInstance());
		this.p = p;
		decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		metadata = new HashMap<>();
		if(editors.containsKey(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas mis à jour le nom du mob. L'ancienne fenetre a été reset.");
			editors.remove(p);
		}
		if(editorsMD.containsKey(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas mis à les métadata du mob. L'ancienne fenetre a été reset.");
			editorsMD.remove(p);
		}
		
		for(int i=0;i<getSize();i++)
			addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(),i);

		entity = new Loop<>(GUIEntityType.values());
		
		updateType(true);
		tauxDrops = new double[] {10, 10, 10, 10};
		drops = new ItemStack[4];
		
		for(int i = 0; i < 4; i++) {
			updateDrop(i, new ItemStack(Material.AIR));
		}
		xp = 500;
		updateXp();
		
		addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(DARK_RED+"Annuler").toItemStack(), 52);
		addOption(new ItemBuilder(Material.EMERALD_BLOCK).setName(DARK_GREEN+"Valider" + (key==null ? " et Créer":"")).toItemStack(), 53);
		
		name = NULL_NAME;
		if(key != null) {
			name = key;
			load(key);
		}
		
		updateName();
		displayMetaData();
		
		show(p);
	}
	
	private void updateName() {
		addOption(new ItemBuilder(Material.NAME_TAG).setName(GREEN+"Changer le nom").setLore(GRAY+"Actuel : [" + RESET+ name + GRAY + "]").toItemStack(), 1);
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		if(!closeToSetName)
			removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null)
			return;
		if(e.getCurrentItem().getType() == Material.AIR)
			return;
		
		if(e.getClickedInventory().equals(getInventory()))
			e.setCancelled(true);

		int line = e.getSlot() / 9 - 1;

		ItemStack item = e.getCurrentItem();
		Material mat = item.getType();
		
		if(mat == Material.BLACK_STAINED_GLASS_PANE || mat == Material.LIGHT_GRAY_STAINED_GLASS_PANE)
			return;
		
		switch(e.getSlot()) {
			case 0:
				entity.next();
				updateType(false);
				return;
			case 1:
				editors.put(p, this);
				closeToSetName = true;
				p.closeInventory();
				p.sendMessage(HalystiaRPG.PREFIX + BLUE + "Entrez le nom du mob "+GOLD+"dans le tchat"+BLUE+" ! Mettez des couleurs avec le symbole [&] !");
				return;
			case 2:
				if(mat == Material.TNT) {
					playSound(Sound.ENTITY_GENERIC_EXPLODE);
					HalystiaRPG.getInstance().getMobManager().getConfig().set(keyMob, null);
					HalystiaRPG.getInstance().getMobManager().saveConfig();
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Mob détruit avec succès.");
					close();
				}
				return;
			case 3:
				updateHead(e.getCursor());
				return;
			case 4:
				updateChestplate(e.getCursor());
				return;
			case 5:
				updateLeggings(e.getCursor());
				return;
			case 6:
				updateBoots(e.getCursor());
				return;
			case 7:
				updateWeapon(e.getCursor());
				return;
			case 8:
				editorsMD.put(p, this);
				closeToSetName = true;
				p.closeInventory();
				p.sendMessage(HalystiaRPG.PREFIX + BLUE + "Edition de metaData. <meta> <data>. data=0 pour effacer.");
				p.sendMessage(HalystiaRPG.PREFIX + DARK_AQUA + EnemyMob.getAllTags());
				return;
			case 13:
			case 22:
			case 31:
			case 40:
				updateDrop(line, e.getCursor());
				return;
			case 45:
			case 46:
			case 47:
				xp -= getValueOfDeltaExperience(e.getCurrentItem().getAmount());
				if(xp < 0)
					xp = 0;
				updateXp();
				return;
			case 49:
			case 50:
			case 51:
				xp += getValueOfDeltaExperience(e.getCurrentItem().getAmount());
				if(xp > 1000000)
					xp = 1000000;
				updateXp();
				return;
		}
		
		if(mat == Material.RED_STAINED_GLASS) {
			if(drops[line].getType() == Material.AIR)
				return;
			tauxDrops[line] -= getValueOfDeltaDropChance(item.getAmount());
			if(tauxDrops[line] < 0)
				tauxDrops[line] = 0;
			updateTauxDrop(line);
			return;
		} else if(mat == Material.LIME_STAINED_GLASS) {
			if(drops[line].getType() == Material.AIR)
				return;
			tauxDrops[line] += getValueOfDeltaDropChance(item.getAmount());
			if(tauxDrops[line] > 100)
				tauxDrops[line] = 100;
			updateTauxDrop(line);
			return;
		} else if(mat == Material.REDSTONE_BLOCK) {
			close();
			return;
		} else if(mat == Material.EMERALD_BLOCK) {
			export();
			playSound(Sound.ENTITY_VILLAGER_TRADE);
			p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Mob édité avec succès.");
			close();
			return;
		}
	}
	
	private void export() {
		FileConfiguration config = HalystiaRPG.getInstance().getMobManager().getConfig();

		String key = "";
		if(keyMob == null) {
			do {
				key = new RandomString(15).nextString();
			} while(config.getKeys(false).contains(key));
		} else {
			key = keyMob;
			config.set(key, null);
			HalystiaRPG.getInstance().getMobManager().saveConfig();
		}
		
		config.set(key + ".type", entity.current().getEntityType().toString());
		config.set(key + ".name", (name.equals(NULL_NAME) ? key : name));
		config.set(key + ".drops.xp", xp);
		if(entity.current().canEquipEquipment()) {
			try {
				if(getInventory().getItem(3).getType() != Material.STONE_BUTTON)
					config.set(key + ".equipment.head", getInventory().getItem(3));
			} catch (NullPointerException e) {}
			if(getInventory().getItem(4).getType() != Material.STONE_BUTTON)
				config.set(key + ".equipment.chest", getInventory().getItem(4));
			if(getInventory().getItem(5).getType() != Material.STONE_BUTTON)
				config.set(key + ".equipment.legs", getInventory().getItem(5));
			if(getInventory().getItem(6).getType() != Material.STONE_BUTTON)
				config.set(key + ".equipment.foot", getInventory().getItem(6));
			if(getInventory().getItem(7).getType() != Material.JUNGLE_BUTTON)
				config.set(key + ".equipment.hand", getInventory().getItem(7));
		}
		
		List<Integer> dropListIndex = new ArrayList<>();
		for(int i = 0; i < drops.length; i++)
			if(drops[i].getType() != Material.AIR)
				dropListIndex.add(i);
		if( ! dropListIndex.isEmpty()) {
			config.set(key + ".drops.amount", dropListIndex.size());
			int j = 1;
			for(int index : dropListIndex) {
				config.set(key + ".drops." + j + ".chances",(int) (tauxDrops[index] * 100));
				config.set(key + ".drops." + j + ".item", drops[index]);
				j++;
			}
		}
		
		for(MetaTag meta : EnemyMob.metaDatas)
			config.set(key+"."+meta.getName(), null);
		for(MetaTag meta : metadata.keySet()) {
			config.set(key+"."+meta.getName(), metadata.get(meta));
		}
		
		HalystiaRPG.getInstance().getMobManager().saveConfig();
	}
	
	private String keyMob = null;
	private void load(String key) {
		this.keyMob = key;
		FileConfiguration config = HalystiaRPG.getInstance().getMobManager().getConfig();
		
		String typeStr = config.getString(key+".type");
		try {
			EntityType type = EntityType.valueOf(typeStr);
			entity.forcePosition(GUIEntityType.getWithEntityType(type));
			updateType(false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		updateHead(config.getItemStack(key+".equipment.head"));
		updateChestplate(config.getItemStack(key+".equipment.chest"));
		updateLeggings(config.getItemStack(key+".equipment.legs"));
		updateBoots(config.getItemStack(key+".equipment.foot"));
		updateWeapon(config.getItemStack(key+".equipment.hand"));
		
		name = config.getString(key+".name");
		updateName();
		
		for(int i = 1; i <= 4; i++) {
			if(!config.contains(key+".drops."+i+".item"))
				break;
			ItemStack item = config.getItemStack(key+".drops."+i+".item");
			tauxDrops[i-1] = config.getDouble(key+".drops."+i+".chances") / 100;
			updateDrop(i-1, item);
		}
		
		xp = config.getInt(key+".drops.xp");
		updateXp();
		
		for(MetaTag meta : EnemyMob.metaDatas) {
			if(config.contains(key + "." + meta.getName())) {
				metadata.put(meta, config.getDouble(key + "." + meta.getName()));
			}
		}
		
		addOption(new ItemBuilder(Material.TNT).setName(DARK_RED+"Détruire le mob").setLore(RED+""+ITALIC+"(Définitif !)").toItemStack(), 2);
	}
	
	private void close() {
		CommandEditMobs.openGUI(p, 1, HalystiaRPG.getInstance());
	}
	
	public void updateDrop(int index, ItemStack item) {
		drops[index] = new ItemStack(item);
		updateTauxDrop(index);
	}
	
	public void updateTauxDrop(int index) {
		String taux = decimalFormat.format(tauxDrops[index]);
		int b = (1+index)*9;
		String lore = LIGHT_PURPLE+"("+taux+" %)";
		if(drops[index].getType() == Material.AIR)
			lore = "";
		addOption(new ItemBuilder(Material.RED_STAINED_GLASS, 64).setName(RED+"- 10%").setLore(lore).toItemStack(), b);
		addOption(new ItemBuilder(Material.RED_STAINED_GLASS, 10).setName(RED+"- 1%").setLore(lore).toItemStack(), b+1);
		addOption(new ItemBuilder(Material.RED_STAINED_GLASS, 5).setName(RED+"- 0.1%").setLore(lore).toItemStack(), b+2);
		addOption(new ItemBuilder(Material.RED_STAINED_GLASS, 1).setName(RED+"- 0.01%").setLore(lore).toItemStack(), b+3);
		addOption(new ItemBuilder(Material.LIME_STAINED_GLASS, 1).setName(GREEN+"+ 0.01%").setLore(lore).toItemStack(), b+5);
		addOption(new ItemBuilder(Material.LIME_STAINED_GLASS, 5).setName(GREEN+"+ 0.1%").setLore(lore).toItemStack(), b+6);
		addOption(new ItemBuilder(Material.LIME_STAINED_GLASS, 10).setName(GREEN+"+ 1%").setLore(lore).toItemStack(), b+7);
		addOption(new ItemBuilder(Material.LIME_STAINED_GLASS, 64).setName(GREEN+"+ 10%").setLore(lore).toItemStack(), b+8);
		ItemBuilder builder = new ItemBuilder(drops[index]);
		if(drops[index].getType() == Material.AIR) {
			builder = new ItemBuilder(Material.BIRCH_BUTTON).setName(WHITE+"[Insérer un drop ici]");
		} else {
			builder.addLoreLine(LIGHT_PURPLE + "Taux de drop = " + GREEN + taux + LIGHT_PURPLE + "%");
		}
		addOption(builder.toItemStack(), 9*(index+1)+4);
	}
	
	public void updateXp() {
		String lore = YELLOW + "" + xp + " XP";
		addOption(new ItemBuilder(Material.RED_CARPET, 64).setName(RED+"- 100").setLore(lore).toItemStack(), 45);
		addOption(new ItemBuilder(Material.RED_CARPET, 32).setName(RED+"- 10").setLore(lore).toItemStack(), 46);
		addOption(new ItemBuilder(Material.RED_CARPET, 1).setName(RED+"- 1").setLore(lore).toItemStack(), 47);
		addOption(new ItemBuilder(Material.GREEN_CARPET, 1).setName(RED+"+ 1").setLore(lore).toItemStack(), 49);
		addOption(new ItemBuilder(Material.GREEN_CARPET, 32).setName(RED+"+ 10").setLore(lore).toItemStack(), 50);
		addOption(new ItemBuilder(Material.GREEN_CARPET, 64).setName(RED+"+ 100").setLore(lore).toItemStack(), 51);
		addOption(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(GOLD+"Gain d'expérience : " + DARK_PURPLE + "" + xp + "" +GOLD + " xp").toItemStack(), 48);
	}
	
	private double getValueOfDeltaDropChance(int amount) {
		double value = 0.01;
		if(amount == 5)
			value = 0.1;
		else if(amount == 10)
			value = 1;
		else if(amount == 64)
			value = 10;
		return value;
	}
	
	private int getValueOfDeltaExperience(int amount) {
		int value = 1;
		if(amount == 32)
			value = 10;
		else if(amount == 64)
			value = 100;
		return value;
	}
	
	public void updateType(boolean firstUpdate) {
		GUIEntityType t = entity.current();
		addOption(t.getIcone(), 0);
		final boolean old = canhaveArmor;
		if(t.canEquipEquipment()) {
			canhaveArmor = true;
			if((!old) || firstUpdate) {
				updateHead(null);
				updateChestplate(null);
				updateLeggings(null);
				updateBoots(null);
				updateWeapon(null);
			}
		} else {
			canhaveArmor = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for(int i =3; i<=7; i++)
						addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(GRAY+"Ce mob ne peut pas avoir d'équipement !").toItemStack(), i);
				}
			});
		}
	}
	
	public void updateHead(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez une tête ou un casque").toItemStack(), 3);
			return;
		}
		addOption(new ItemStack(item), 3);
	}
	
	public void updateChestplate(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez un plastron").toItemStack(), 4);
			return;
		}
		if(( ! Equipment.CHESTPLATE.hasObject(item.getType()))) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez un plastron").toItemStack(), 4);
			playSound(Sound.ENTITY_VILLAGER_NO);
			return;
		}
		addOption(new ItemStack(item), 4);
	}

	public void updateLeggings(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez des jambières").toItemStack(), 5);
			return;
		}
		if(( ! Equipment.LEGGINGS.hasObject(item.getType()))) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez des jambières").toItemStack(), 5);
			playSound(Sound.ENTITY_VILLAGER_NO);
			return;
		}
		addOption(new ItemStack(item), 5);
	}
	
	public void updateBoots(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez des bottes").toItemStack(), 6);
			return;
		}
		if(( ! Equipment.BOOTS.hasObject(item.getType()))) {
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Insérez des bottes").toItemStack(), 6);
			playSound(Sound.ENTITY_VILLAGER_NO);
			return;
		}
		addOption(new ItemStack(item), 6);
	}
	
	public void updateWeapon(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.JUNGLE_BUTTON).setName(GRAY+"Insérez une arme").toItemStack(), 7);
			return;
		}
		if(item.getType() == Material.AIR || item.getType() == Material.JUNGLE_BUTTON) {
			addOption(new ItemBuilder(Material.JUNGLE_BUTTON).setName(GRAY+"Insérez une arme").toItemStack(), 7);
			playSound(Sound.ENTITY_VILLAGER_NO);
			return;
		}
		addOption(new ItemStack(item), 7);
	}
	
	public void reopenWithMetaData(String meta, String data) {
		closeToSetName = false;
		editorsMD.remove(p);
		try {
			double value = Double.parseDouble(data);
			if(value < 0)
				value = 0;
			else if(value > 1000000)
				value = 1000000;
			updateMetaData(meta, value);
			displayMetaData();
		} catch (Exception e) {
			p.sendMessage(RED+"La valeur ["+data+"] est incorrecte ! Il doit s'agir d'un nombre.");
		}
		show(p);
	}
	
	private void updateMetaData(String meta, double data) {
		MetaTag tag = EnemyMob.getTag(meta);
		if( tag == null) {
			p.sendMessage(ChatColor.RED + "Le nom de metadata [" + meta + "] n'existe pas.");
			return;
		}
		if( ! metadata.containsKey(tag)) {
			metadata.put(tag, data);
			p.sendMessage(ChatColor.AQUA + "MetaData [" + ChatColor.GOLD + meta + ChatColor.AQUA + "] set à [" + ChatColor.GREEN + data + ChatColor.AQUA + "].");
			return;
		}
		if(data == 0) {
			metadata.remove(tag);
			p.sendMessage(ChatColor.AQUA + "MetaData [" + ChatColor.GOLD + meta + ChatColor.AQUA + "] supprimée.");
			return;
		}
		metadata.replace(tag, data);
		p.sendMessage(ChatColor.AQUA + "MetaData [" + ChatColor.GOLD + meta + ChatColor.AQUA + "] mise à jour à [" + ChatColor.GREEN + data + ChatColor.AQUA + "].");
	}
	
	private void displayMetaData() {
		ItemBuilder builder = new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "MetaData");
		if( ! metadata.isEmpty()) {
			for(MetaTag meta : metadata.keySet())
				builder.addLoreLine(ChatColor.AQUA + "[" + meta.getName() + "] = [" + ChatColor.GOLD + meta.getValue(metadata.get(meta)) + ChatColor.AQUA + "]");
		} else {
			builder.setLore("(Aucune)");
		}
		builder.addLoreLine(" ").addLoreLine(ChatColor.WHITE + "Cliquez pour modifier !");
		addOption(builder.toItemStack(), 8);
	}
	
	public void reopen(String mobName) {
		closeToSetName = false;
		editors.remove(p);
		try {
			name = ChatColor.translateAlternateColorCodes('&', mobName);
			updateName();
		} catch (Exception e) {
			p.sendMessage(RED+"Le nom est incorrect !");
		}
		show(p);
	}
	
	private void playSound(Sound sound) {
		p.playSound(p.getLocation(), sound, 1.4f, .8f);
	}

}
