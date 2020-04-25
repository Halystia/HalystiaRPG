package fr.jamailun.halystia.npcs;

import java.util.List;

public interface Dialogable {

	public List<String> getDialog();
	
	public void clearDialog();
	
	public boolean setDialogLine(String text, int line);

	public void addDialogLine(String text);
	
	public boolean removeDialogLine(int line);
	
	public boolean insertDialogLine(String string, int line);
	
}
