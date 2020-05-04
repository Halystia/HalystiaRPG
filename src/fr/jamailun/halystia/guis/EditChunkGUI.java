package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.BLACK;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.DARK_BLUE;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.chunks.ChunkType;
import fr.jamailun.halystia.commands.CommandEditChunks;
import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;
import fr.jamailun.halystia.utils.RandomString;

public class EditChunkGUI extends MenuGUI {

	private final Player p;
	private boolean closeToSetName = false;
	private boolean closeToSetMob = false;
	
	private int[] chancesSpawns;
	private String[] mobRefs;
	private String name;

	private Map<MetaTag, String> metadata;
	
	private final DecimalFormat decimalFormat;
	
	public static HashMap<Player, EditChunkGUI> editors = new HashMap<>();
	public static HashMap<Player, EditChunkGUI> editorsMD = new HashMap<>();
	
	public EditChunkGUI(Player p) {
		this(p, null);
	}
	
	public EditChunkGUI(Player p, ChunkType type) {
		super(DARK_BLUE+(type==null?"Créer un nouveau":"Editer un")+" chunk", 9*6, HalystiaRPG.getInstance());
		this.p = p;
		decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		metadata = new HashMap<>();
		
		if(editors.containsKey(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas mis à jour le nom du chunk. L'ancienne fenetre a été reset.");
			editors.remove(p);
		}
		if(editorsMD.containsKey(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Vous n'avez pas mis à les métadata du chunk. L'ancienne fenetre a été reset.");
			editorsMD.remove(p);
		}
		
		for(int i=0;i<getSize();i++)
			addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(),i);

		mobRefs = new String[9];
		chancesSpawns = new int[] {10,10,10, 10,10,10, 10,10,10};
		
		updateIcone(null);
		
		for(int i = 0; i < mobRefs.length; i++) {
			updateMob(i, null);
		}
		
		addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(DARK_RED+"Annuler").toItemStack(), 7);
		addOption(new ItemBuilder(Material.EMERALD_BLOCK).setName(DARK_GREEN+"Valider" + (type==null ? " et créer":"")).toItemStack(), 8);
		do {
			name = new RandomString(10).nextString();
		} while (HalystiaRPG.getInstance().getChunkCreator().getChunkType(name) != null);
		if(type != null) {
			name = type.getName();
			load(type);
		}
		
		updateName();
		updateAllChances();
		displayMetaData();
		
		show(p);
	}
	
	
	
	private void updateName() {
		addOption(new ItemBuilder(Material.NAME_TAG).setName(GREEN+"Changer le nom").setLore(GRAY+"Actuel : [" + RESET+ name + GRAY + "]").toItemStack(), 1);
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		if(!closeToSetName && !closeToSetMob)
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
		int col = e.getSlot() % 9;

		ItemStack item = e.getCurrentItem();
		Material mat = item.getType();
		
		if(mat == Material.BLACK_STAINED_GLASS_PANE || mat == Material.LIGHT_GRAY_STAINED_GLASS_PANE)
			return;
		
		switch(e.getSlot()) {
			case 0:
				updateIcone(e.getCursor());
				return;
			case 1:
				editors.put(p, this);
				closeToSetName = true;
				p.closeInventory();
				p.sendMessage(HalystiaRPG.PREFIX + BLUE + "Entrez le nom du chunk "+GOLD+"dans le tchat"+BLUE+". N'utilisez pas de caractères compliqués.");
				return;
			case 5:
				if(mat == Material.TNT) {
					playSound(Sound.ENTITY_GENERIC_EXPLODE);
					HalystiaRPG.getInstance().getChunkCreator().removeChunkType(originalName);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Chunk détruit avec succès.");
					close();
				}
				return;
			case 6:
				editorsMD.put(p, this);
				closeToSetName = true;
				p.closeInventory();
				p.sendMessage(HalystiaRPG.PREFIX + BLUE + "Edition de metaData. <meta> <data>. data=0 pour effacer.");
				p.sendMessage(HalystiaRPG.PREFIX + DARK_AQUA + ChunkType.getAllTags());
				return;
			case 8:
				export();
				playSound(Sound.ENTITY_VILLAGER_TRADE);
				p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Chunk édité avec succès.");
				close();
				return;
			case 7:
				close();
				return;
		}
		
		if(line == 0 || line == 1) {
			chancesSpawns[col] += e.getCurrentItem().getAmount();
			updateAllChances();
		} else if(line == 3 || line == 4) {
			chancesSpawns[col] -= e.getCurrentItem().getAmount();
			updateAllChances();
		} else if(line == 2) {
			openUpdateMob(col);
		}
		
	}
	
	private void export() {
		HashMap<String, Integer> spawns = new HashMap<>();
		for(int i = 0; i < mobRefs.length; i++) {
			if(mobRefs[i] != null)
				if( ! mobRefs[i].isEmpty())
					spawns.put(mobRefs[i], chancesSpawns[i]);
		}
		if(originalName != null) {
			HalystiaRPG.getInstance().getChunkCreator().updateChunkType(originalName, name, getInventory().getItem(0).getType(), spawns, metadata);
			return;
		}
		HalystiaRPG.getInstance().getChunkCreator().createChunkType(name, getInventory().getItem(0).getType(), spawns, metadata);
		
	}
	
	private String originalName = null;
	private void load(ChunkType type) {
		updateIcone(type.getIcone());
		name = type.getName();
		originalName = name;
		updateName();
		int i = 0;
		for(String mobRef : type.getSpawnPossibilities().keySet()) {
			chancesSpawns[i] =  type.getSpawnPossibilities().get(mobRef);
			updateMob(i, mobRef);
			i++;
		}
		FileConfiguration config = HalystiaRPG.getInstance().getChunkCreator().getConfig();
		for(MetaTag meta : ChunkType.metaDatas) {
			//System.out.println(type.getName() + "." + meta.getName()+" -> " + config.getString(type.getName() + "." + meta.getName()));
			if(config.contains(type.getName() + "." + meta.getName())) {
				metadata.put(meta, config.getString(type.getName() + "." + meta.getName()));
			}
		}
		displayMetaData();
		addOption(new ItemBuilder(Material.TNT).setName(DARK_RED+"Détruire le chunk").setLore(RED+""+ITALIC+"(Définitif !)").toItemStack(), 5);
	}
	
	private void close() {
		CommandEditChunks.openGUI(p, 1, HalystiaRPG.getInstance());
	}
	
	public void openUpdateMob(int index) {
		closeToSetMob = true;
		openChooseMob(index, 1);
	}
	
	public void updateMob(int index, String mobRef) {
		mobRefs[index] = mobRef;
		updateChances(index);
	}
	
	private double getTotalChances() {
		double total = 0;
		for(int i = 0; i < mobRefs.length; i++) {
			if(mobRefs[i] == null)
				continue;
			if(mobRefs[i].isEmpty())
				continue;
			total += chancesSpawns[i];
		}
		return total;
	}
	
	public void updateAllChances() {
		for(int i =0; i < mobRefs.length; i++)
			updateChances(i);
	}
	
	public void updateChances(int index) {
		if(chancesSpawns[index] < 0)
			chancesSpawns[index] = 0;
		else if(chancesSpawns[index] > 1000)
			chancesSpawns[index] = 1000;
		double proba = (chancesSpawns[index] / getTotalChances()) * 100;
		if(chancesSpawns[index] == 0)
			proba = 0;
		String lore = GRAY+""+ITALIC+"(x "+chancesSpawns[index]+")";
		String lore2 = YELLOW+""+decimalFormat.format(proba)+" %";
		if(mobRefs[index] == null) {
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName(DARK_GRAY+"Rajoutez un mob !").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*1));
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName(DARK_GRAY+"Rajoutez un mob !").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*2));
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName(DARK_GRAY+"Rajoutez un mob !").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*4));
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName(DARK_GRAY+"Rajoutez un mob !").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*5));
			addOption(new ItemBuilder(Material.STONE_BUTTON).setName(GRAY+"Rajoutez un mob !").toItemStack(), index + (9*3));
		} else {
			addOption(new ItemBuilder(Material.GREEN_STAINED_GLASS, 10).setName(GREEN+"+ 10").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*1));
			addOption(new ItemBuilder(Material.GREEN_STAINED_GLASS).setName(GREEN+"+ 1").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*2));
			addOption(new ItemBuilder(Material.RED_STAINED_GLASS).setName(RED+"- 1").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*4));
			addOption(new ItemBuilder(Material.RED_STAINED_GLASS, 10).setName(RED+"- 10").setLore(lore).addLoreLine(lore2).toItemStack(), index + (9*5));
			addOption(new ItemBuilder(((HalystiaRPG)main).getMobManager().getIconeOfMob(mobRefs[index])).addLoreLine(lore).addLoreLine(lore2).toItemStack(), index + (9*3));
		}
	}
	
	public void updateIcone(ItemStack item) {
		if(item == null) {
			addOption(new ItemBuilder(Material.GRASS_BLOCK).setName(BLUE+"Îcone").setLore(GRAY+"Faites glisser un objet pour changer !").toItemStack(), 0);
			return;
		}
		if(item.getType() == Material.AIR || item.getType() == Material.STONE_BUTTON || item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
			addOption(new ItemBuilder(Material.GRASS_BLOCK).setName(BLUE+"Îcone").setLore(GRAY+"Faites glisser un objet pour changer !").toItemStack(), 0);
			playSound(Sound.ENTITY_VILLAGER_NO);
			return;
		}
		addOption(new ItemBuilder(item.getType()).setName(BLUE+"Îcone").setLore(GRAY+"Faites glisser un objet pour changer !").toItemStack(), 0);
	}
	
	public void reopen(String chunkName) {
		closeToSetName = false;
		editors.remove(p);
		try {
			if(name.contains(" ") || name.contains("&")) {
				p.sendMessage(RED+"Le nom est incorrect ! Il contient des caractères non autorisés.");
				show(p);
				return;
			}
			name = chunkName;
			updateName();
		} catch (Exception e) {
			p.sendMessage(RED+"Le nom est incorrect !");
		}
		show(p);
	}
	
	private void openChooseMob(int index, int page) {
		FileConfiguration config = HalystiaRPG.getInstance().getMobManager().getConfig();
		List<String> keys = new ArrayList<>();
		for(String key : config.getKeys(false))
			keys.add(key);
		int maxPages = 1 + (keys.size() / 45);
		
		final int debut = ((page-1)*45);
		
		MenuGUI gui = new MenuGUI(DARK_BLUE + "Liste des mobs customisés", 9*6, main) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getCurrentItem() == null)
					return;
				if(e.getCurrentItem().getType() == Material.AIR)
					return;
				if(e.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE || e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE)
					return;
				
				if(e.getSlot() > 9*5) {
					Material mat = e.getCurrentItem().getType();
					if(mat == Material.BLUE_CONCRETE)
						openChooseMob(index, page+1);
					else if(mat == Material.YELLOW_CONCRETE)
						openChooseMob(index, page-1);
					else if(mat == Material.TNT)
						openAndClose(null);
					else if(mat == Material.BARRIER) {
						closeToSetMob = false;
						reopen(name);
					}
					return;
				}
				
				String mobRef = keys.get(debut + e.getSlot());
				openAndClose(mobRef);
			}
			
			private void openAndClose(String mobRef) {
				closeToSetMob = false;
				updateMob(index, mobRef);
				reopen(name);
			}
		};
		for(int i=0;i<gui.getSize()-9;i++)
			gui.addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(),i);
		for(int i=gui.getSize()-9;i<gui.getSize();i++)
			gui.addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(BLACK+"").toItemStack(),i);
		
		
		int j = debut;
		for(String key : keys) {
			if(j >= keys.size())
				break;
			gui.addOption(HalystiaRPG.getInstance().getMobManager().getIconeOfMob(key), j);
			j++;
		}
		
		
		if(page < maxPages)
			gui.addOption(new ItemBuilder(Material.BLUE_CONCRETE).setName(GRAY+"Vers la page " + DARK_PURPLE + "" + (page+1)).setLore(DARK_GRAY +"Actuellement page " + page).toItemStack(),(9*6)-1);
		if(page > 1)
			gui.addOption(new ItemBuilder(Material.YELLOW_CONCRETE).setName(GRAY+"Vers la page " + DARK_PURPLE + "" + (page-1)).setLore(DARK_GRAY +"Actuellement page " + page).toItemStack(),(9*6)-9);
		
		gui.addOption(new ItemBuilder(Material.BARRIER).setName(RED+"Retour").toItemStack(), (9*6)-6);
		gui.addOption(new ItemBuilder(Material.TNT).setName(GOLD+"Enlever le mob sélectionné").toItemStack(), (9*6)-4);
		
		gui.show(p);
	}
	
	private void playSound(Sound sound) {
		p.playSound(p.getLocation(), sound, 1.4f, .8f);
	}

	private void updateMetaData(String meta, String data) {
		MetaTag tag = ChunkType.getTag(meta);
		if( tag == null) {
			p.sendMessage(ChatColor.RED + "Le nom de metadata [" + meta + "] n'existe pas.");
			return;
		}
		if( ! metadata.containsKey(tag)) {
			metadata.put(tag, data);
			p.sendMessage(ChatColor.AQUA + "MetaData [" + ChatColor.GOLD + meta + ChatColor.AQUA + "] set à [" + ChatColor.GREEN + data + ChatColor.AQUA + "].");
			return;
		}
		if(data == "null" || data.isEmpty()) {
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
		addOption(builder.toItemStack(), 6);
	}

	public void reopenWithMetaData(String meta, String string) {
		closeToSetName = false;
		editorsMD.remove(p);
		if(string.isEmpty())
			string = "null";
		else if(string.equals("1"))
			string = "true";
		else if(string.equals("0"))
			string = "false";
		updateMetaData(meta, string);
		displayMetaData();
		show(p);
	}
}