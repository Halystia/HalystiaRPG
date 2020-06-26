package fr.jamailun.spellParser;

import java.io.*;

public class SpellAnalyzer {

	private final SpellData data;
	private SpellTokenizer tokenizer;

	public SpellAnalyzer(File file) {
		data = new SpellData();
		tokenizer = new SpellTokenizer();
		deserialize(file);
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

}