package fr.jamailun.halystia;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static  fr.jamailun.halystia.utils.FileDataRPG.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;

public class ModuleLoader extends FileDataRPG {
	
	private final HalystiaRPG main;
	private final Map<Module, Boolean> modules;
	private String world = "RolePlay";
	private List<String> dungeons = new ArrayList<>();
	private String prefix = GOLD + "" + BOLD + "R" + GOLD + "ole" + BOLD + "P" + GOLD + "lay" + WHITE + " | ";
	
	public ModuleLoader(HalystiaRPG main, String path) {
		super(path, "config");
		this.main = main;
		modules = new HashMap<>();
		loadConfiguration();
	}
	
	public void loadConfiguration() {
		defaultConfig();
		prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
		modules.clear();
		dungeons.clear();
		for(Module module : Module.values())
			modules.put(module, false);
		int n = 0;
		for(String str : config.getStringList("modules")) {
			try {
				Module mod = Module.valueOf(str);
				modules.replace(mod, true);
				n++;
			} catch (IllegalArgumentException e) {
				main.getConsole().sendMessage(RED + "Module '"+str+"' isn't recognized.");
			}
		}
		main.getConsole().sendMessage(prefix + GREEN + "Loaded " + n + " module"+(n>1?"s":"")+".");
		for(String d : config.getStringList("dungeons-map")) {
			if( Bukkit.getWorld(d) == null ) {
				main.getConsole().sendMessage(RED + "Map '"+d+"' doesn't exists.");
				continue;
			}
			dungeons.add(d);
		}
		world = config.getString("main-map");
		if( Bukkit.getWorld(world) == null ) {
			main.getConsole().sendMessage(prefix+ DARK_RED + "[FATAL ERROR] Main map '"+world+"' doesn't exists.");
			main.getConsole().sendMessage(prefix+ DARK_RED + "[FATAL ERROR] Stopping plugin.");
			Bukkit.getPluginManager().disablePlugin(main);
			return;
		}
		main.getConsole().sendMessage(prefix + GREEN + "Loaded prefix, main map ("+world+") and "+( dungeons.size()>0? (dungeons.size()+" dungeon map"+(dungeons.size()>1?"s":"")) : ("no dungeon map"))+".");
	}
	
	private void defaultConfig() {
		if( ! config.contains("prefix") )
			config.set("prefix", "");
		if( ! config.contains("main-map") )
			config.set("main-map", "RolePlay");
		if( ! config.contains("dungeons-maps") )
			config.set("dungeons-maps", new ArrayList<String>());
		if( ! config.contains("modules") )
			config.set("modules", Module.list());
		save();
	}
	
	public enum Module {
		TITLE,
		MOBS,
		NPCS,
		QUESTS,
		AUBERGISTES,
		BANQUE,
		DONJONS,
		JOBS,
		SPELLS,
		SHOPS,
		CLASSE,
		FISHING,
		BOATS,
		POTIONS,
		ROYAUMES;
		
		public static List<String> list() {
			return Arrays.asList(values()).stream().map(module -> module.toString()).collect(Collectors.toList());
		}
	}
}