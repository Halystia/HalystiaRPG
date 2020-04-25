package fr.jamailun.halystia.utils;

public class Loop<T> {
	
	private T[] array;
	private int current;
	
	public Loop(T[] array) {
		this.array = array;
		current = 0;
	}
	
	public void next() {
		current++;
		if(current == array.length)
			current = 0;
	}
	
	public T current() {
		return array[current];
	}
	
	public T nextAndCurrent() {
		next();
		return current();
	}
	
	public void forcePosition(T position) {
		for(int i=0; i<array.length; i++) {
			if(array[i].equals(position)) {
				current = i;
				return;
			}
		}
	}
	
}
