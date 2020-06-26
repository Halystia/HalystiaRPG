package fr.jamailun.halystia.spells;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;

import fr.jamailun.spellParser.SpellAnalyzer;

public class SpellLoader {
	
	private final String directory;
	
	private final Map<String, SpellAnalyzer> spells;
	
	public SpellLoader(String directory) {
		File dir = new File(directory);
		if( ! dir.exists())
			dir.mkdirs();
		this.directory = directory;
		spells = new HashMap<>();
		reloadSpells();
	}
	
	public void reloadSpells() {
		spells.clear();
		try {
			Files.walk(Paths.get(directory)).filter(Files::isRegularFile).filter(f -> f.getFileName().endsWith(".spell")).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				spells.put(name, new SpellAnalyzer(f.toFile()));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}