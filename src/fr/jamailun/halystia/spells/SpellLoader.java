package fr.jamailun.halystia.spells;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;

import fr.jamailun.spellParser.SpellAnalyzer;

public class SpellLoader {
	
	private final String directory;
	private final SpellManager manager;
	
	public SpellLoader(String directory, SpellManager manager) {
		this.manager = manager;
		File dir = new File(directory);
		if( ! dir.exists())
			dir.mkdirs();
		this.directory = directory;
		reloadSpells();
	}
	
	public void reloadSpells() {
		manager.clearNotLegacy();;
		try {
			Files.walk(Paths.get(directory)).filter(Files::isRegularFile).filter(f -> f.toFile().getAbsolutePath().endsWith(".spell")).forEach(f -> {
				//String name = FilenameUtils.removeExtension(f.toFile().getName());
				System.out.println("Read not legacy spell '"+FilenameUtils.removeExtension(f.toFile().getName()));
				SpellAnalyzer spell = new SpellAnalyzer(f.toFile());
				if(spell.isValid())
					manager.registerSpell(spell);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}