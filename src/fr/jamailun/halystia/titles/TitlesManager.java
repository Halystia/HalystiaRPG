package fr.jamailun.halystia.titles;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fr.jamailun.halystia.utils.FileDataRPG;

public class TitlesManager extends FileDataRPG {

	private Set<Title> titles;
	
	public TitlesManager(String path) {
		super(path, "titles");
		titles = new HashSet<>();
		reload();
	}
	
	public void reload() {
		titles.clear();
		for(String key : config.getKeys(false))
			titles.add(new Title(key, config.getString(key)));
	}
	
	public boolean createTitle(String tag, String display) {
		if(getTitleWithTag(tag) != null)
			return false;
		synchronized (file) {
			config.set(tag, display);
			save();
			titles.add(new Title(tag, display));
		}
		return true;
	}
	
	public boolean removeTitle(String tag) {
		Title title = getTitleWithTag(tag);
		if(title == null)
			return false;
		synchronized (file) {
			titles.remove(title);
			config.set(tag, null);
			save();
		}
		return true;
	}
	
	public Set<Title> getTitlesWithTags(Collection<String> tags) {
		return titles.stream().filter(title -> tags.contains(title.getTag())).collect(Collectors.toSet());
	}
	
	public Title getTitleWithTag(String tag) {
		for(Title title : titles)
			if(title.getTag().equals(tag))
				return title;
		return null;
	}

	public int getSize() {
		return titles.size();
	}
	
	public Set<Title> getAllTitles() {
		return new HashSet<>(titles);
	}

}