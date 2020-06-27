package fr.jamailun.spellParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellAnalyzer extends Spell {

	private final SpellData data;
	private SpellTokenizer tokenizer;

	public SpellAnalyzer(File file) {
		data = new SpellData();
		tokenizer = new SpellTokenizer();
		deserialize(file);
	}
	
	public SpellData getSpellData() {
		return data;
	}
	
	private boolean deserialize(File file) {
		if(file == null || ! file.exists())
			return false;

		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int n = 0;
			boolean data = true;
			while ((line = reader.readLine()) != null) {
				n++;
				if (line.startsWith("#"))
					continue;
				if (line.startsWith("{")) {
					data = false;
					System.out.println("Data Completed. Data : "+this.data.toString());
					continue;
				}
				if (line.startsWith("}")) {
					if(tokenizer.readLine(line, n))
						System.out.println("End of deserialization.");
					break;
				}
				if (data) {
					readData(line);
					continue;
				}

				tokenizer.readLine(line, n);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if( ! tokenizer.isFinished()) {
			System.err.println("Error : block is not finished (after deserialization).");
			return false;
		}

		if( ! tokenizer.isInLastPart()) {
			System.err.println("Error : parsing error : missing a '}'.");
			return false;
		}

		return true;
	}

	private void readData(String line) {
		String[] words = line.split(": ", 2);
		if(words.length < 2) {
			System.err.println("Erreur ligne data ["+line+"].");
			return;
		}
		String a = words[0];
		if(SpellData.isIntKey(a)) {
			int value;
			try {
				value = Integer.parseInt(words[1]);
			} catch(NumberFormatException e) {
				System.err.println("Erreur ligne, mauvais format de nombre entier ["+line+"].");
				return;
			}
			data.associateInteger(a, value);
			return;
		}
		if(SpellData.isStringKey(a)) {
			data.associateString(a, words[1]);
			return;
		}
		System.err.println("Unknown key : '" + a + "'.");
	}

	@Override
	public boolean cast(Player p) {
		if( ! data.hasId()) {
			p.sendMessage(ChatColor.RED + "Une erreur est survenue... le spell n'a pas d'ID !");
			return false;
		}
		tokenizer.run(p);
		return true;
	}

	@Override
	public String getName() {
		return data.getName();
	}

	@Override
	public ChatColor getColor() {
		try {
		return ChatColor.valueOf(data.getColor());
		} catch (IllegalArgumentException e) {
			return ChatColor.GRAY;
		}
	}
	
	@Override
	public final boolean isLegacy() {
		return false;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.fromString(data.getClasse());
	}

	@Override
	public int getLevelRequired() {
		return data.getLevel();
	}

	@Override
	public List<String> getLore() {
		return data.getLore();
	}

	@Override
	public String getStringIdentification() {
		return data.getId();
	}

	@Override
	public int getManaCost() {
		return data.getMana();
	}

	@Override
	public int getCooldown() {
		return data.getCooldown();
	}

	public boolean isValid() {
		return data.hasId() && tokenizer.isFinished();
	}

}