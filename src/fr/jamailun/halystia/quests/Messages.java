package fr.jamailun.halystia.quests;

import java.util.ArrayList;
import java.util.List;

import fr.jamailun.halystia.npcs.Dialogable;

public class Messages implements Dialogable {
	
	private List<String> dialog;
	
	public Messages(List<String> msgs) {
		dialog = new ArrayList<>(msgs);
	}
	
	public List<String> getDialog() {
		return dialog;
	}
	
	public void clearDialog() {
		dialog.clear();
	}
	
	public boolean setDialogLine(String text, int line) {
		if(line < 0 || line >= dialog.size())
			return false;
		dialog.set(line, text);
		return true;
	}

	public void addDialogLine(String text) {
		dialog.add(text);
	}
	
	public boolean removeDialogLine(int line) {
		if(line < 0 || line >= dialog.size())
			return false;
		dialog.remove(line);
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
	
	public int getLenght() {
		return dialog.size();
	}
	
}