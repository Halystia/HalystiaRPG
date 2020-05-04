package fr.jamailun.halystia.chunks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.utils.FileDataRPG;

public class ChunkCreator extends FileDataRPG {
	
	private List<ChunkType> types;
	
	public ChunkCreator(String path, String name) {
		super(path, name);
		types = new ArrayList<>();
		for(String key : config.getKeys(false)) {
			ChunkType type = new ChunkType(key, this);
			types.add(type);
		}
	}
	
	public void updateChunkType(final String oldName, String name, Material icon, HashMap<String, Integer> spawnsChances, Map<MetaTag, String> metadata) {
		ChunkType type = getChunkType(oldName);
		config.set(oldName, null);
		type.name = name;
		type.icon = icon;
		type.possiblesSpawns = new HashMap<>(spawnsChances);
		
		for(MetaTag meta : ChunkType.metaDatas)
			config.set(name+"."+meta.getName(), null);
		for(MetaTag meta : metadata.keySet()) {
			config.set(name+"."+meta.getName(), metadata.get(meta));
		}
		type.update(this);
		HalystiaRPG.getInstance().getSpawnChunkManager().replaceValues(oldName, name);
	}
	
	public void createChunkType(String name, Material icon, HashMap<String, Integer> spawnsChances, Map<MetaTag, String> metadata) {
		ChunkType type = new ChunkType(name, spawnsChances, icon, this);
		types.add(type);
		for(MetaTag meta : ChunkType.metaDatas)
			config.set(name+"."+meta.getName(), null);
		for(MetaTag meta : metadata.keySet()) {
			config.set(name+"."+meta.getName(), metadata.get(meta));
		}
		saveConfig();
	}
	
	public List<ChunkType> getChunkTypeList() {
		return new ArrayList<>(types);
	}
	
	public ChunkType getChunkType(final String name) {
		for(ChunkType type : types) {
			if(type.getName().equals(name))
				return type;
		}
		return null;
	}
	
	public void removeChunkType(String name) {
		int i = -1;
		for(int j = 0; j < types.size(); j++) {
			if(types.get(j).getName().equals(name)) {
				i = j;
				break;
			}
		}
		if(i == -1)
			throw new IllegalArgumentException("ID de chunkType inconnu : [" + name + "].");
		types.remove(i);
		config.set(name, null);
		save();
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	void saveConfig() {
		save();
	}

}
