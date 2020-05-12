package fr.jamailun.halystia.enemies.mobSpawner;

public enum MobSpawnerType {
	
	// delay, range, playerRange, spawnCount, maxNearyEntities
	
	SNIPER(20, 2, 30, 1, 2),
	SMALL(10, 4, 15, 2, 10),
	NORMAL(6, 3, 10, 2, 4),
	MASSIVE_10(2, 6, 20, 4, 20),
	;
	
	public final int delay, range, playerRange, spawnCount, maxNearbyEntities;
	
	private MobSpawnerType(int delay, int range, int playerRange, int spawnCount, int maxNearbyEntities) {
		this.delay = delay;
		//this.delay = 1;
		this.range = range;
		this.playerRange = playerRange;
		this.spawnCount = spawnCount;
		this.maxNearbyEntities = maxNearbyEntities;
	}
	
}
