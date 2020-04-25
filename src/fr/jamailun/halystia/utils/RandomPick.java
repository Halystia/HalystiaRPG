package fr.jamailun.halystia.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author jamailun
 * @version 1.0
 * @param <T> : type of pick possible
 */
public class RandomPick<T> {
	
	private List<Element<T>> all;
	
	/**
	 * Create a new RandomPick.
	 * @param elements : Map of T elements, and and Integer that represent the chances of the element to be picked
	 */
	public RandomPick(Map<T, Integer> elements) {
		all = new ArrayList<>();
		for(T object : elements.keySet())
			for(int i = 1; i <= elements.get(object); i++)
				all.add(new Element<T>(object));
	}
	
	public T nextPick() {
		int t = new Random().nextInt(all.size());
		return all.get(t).getObject();
	}
	
	public void addPick(T pick) {
		all.add(new Element<T>(pick));
	}
	
	public void addPick(T pick, int chances) {
		for(int i=0;i<chances;i++)
			all.add(new Element<T>(pick));
	}
	
	private final class Element<K> {
		private final K object;
		public Element(K object) {
			this.object = object;
		}
		public K getObject() {
			return object;
		}
	}
}
