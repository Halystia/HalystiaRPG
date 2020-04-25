package fr.jamailun.halystia.enemies.mobSpawner;

public enum MobSpawnerType {
	
	// delay, range, playerRange, spawnCount, maxNearyEntities
	
	SNIPER(20, 2, 30, 1, 2),
	NORMAL(6, 4, 12, 3, 6),
	MASSIVE_10(2, 6, 16, 2, 16),
	;
	
	public final int delay, range, playerRange, spawnCount, maxNearbyEntities;
	
	private MobSpawnerType(int delay, int range, int playerRange, int spawnCount, int maxNearbyEntities) {
		this.delay = delay;
		this.range = range;
		this.playerRange = playerRange;
		this.spawnCount = spawnCount;
		this.maxNearbyEntities = maxNearbyEntities;
	}
	
}
