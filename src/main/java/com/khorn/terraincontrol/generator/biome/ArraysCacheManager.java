package com.khorn.terraincontrol.generator.biome;

@SuppressWarnings("rawtypes")
public class ArraysCacheManager {

	private static final ArraysCache[] ARRAYS_CACHES = new ArraysCache[4];

	static {
		for (int i = 0; i < ARRAYS_CACHES.length; i++)
			ARRAYS_CACHES[i] = new ArraysCache();
	}

	public static ArraysCache GetCache() {
		for (ArraysCache cache : ARRAYS_CACHES) {
			if (cache.isFree) {
				cache.isFree = false;
				return cache;
			}
		}
		return null;
	}

	public static void ReleaseCache(ArraysCache cache) {
		cache.release();
	}

	private ArraysCacheManager() {}
}
