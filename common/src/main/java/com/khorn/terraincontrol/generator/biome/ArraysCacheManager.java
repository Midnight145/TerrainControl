package com.khorn.terraincontrol.generator.biome;

@SuppressWarnings("rawtypes")
public class ArraysCacheManager {

	private static final ArraysCache[] ARRAYS_CACHES = new ArraysCache[4];

	static {
		for (int i = 0; i < ARRAYS_CACHES.length; i++)
			ARRAYS_CACHES[i] = new ArraysCache();

	}

	public static ArraysCache GetCache() {
		synchronized (ARRAYS_CACHES) {
			for (ArraysCache ArraysCache : ARRAYS_CACHES) {
				if (ArraysCache.isFree) {
					ArraysCache.isFree = false;
					return ArraysCache;
				}
			}

		}
		return null; // Exception ??
	}

	public static void ReleaseCache(ArraysCache cache) {
		synchronized (ARRAYS_CACHES) {
			cache.release();
		}
	}

	/*
	 * @SuppressWarnings({"unchecked"})
	 * public static int[] GetArray(int cacheId, int size)
	 * {
	 * if (size <= 256)
	 * {
	 * int[] array = SmallArrays[cacheId][SmallArraysNext[cacheId]];
	 * if (array == null)
	 * {
	 * array = new int[256];
	 * SmallArrays[cacheId][SmallArraysNext[cacheId]] = array;
	 * }
	 * SmallArraysNext[cacheId]++;
	 * 
	 * return array;
	 * }
	 * int[] array;
	 * if (BigArraysNext[cacheId] == BigArrays[cacheId].size())
	 * {
	 * array = new int[size];
	 * BigArrays[cacheId].add(array);
	 * } else
	 * {
	 * array = (int[]) BigArrays[cacheId].get(BigArraysNext[cacheId]);
	 * if (array.length < size)
	 * {
	 * array = new int[size];
	 * BigArrays[cacheId].set(BigArraysNext[cacheId], array);
	 * }
	 * }
	 * 
	 * BigArraysNext[cacheId]++;
	 * return array;
	 * }
	 */

	private ArraysCacheManager() {}
}