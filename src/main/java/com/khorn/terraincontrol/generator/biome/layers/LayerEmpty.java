package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

import java.util.Arrays;

public class LayerEmpty extends Layer {

	public LayerEmpty(long seed) {
		super(seed);
	}

	@Override
	public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize) {
		int[] thisInts = cache.getArray(xSize * zSize);
        Arrays.fill(thisInts, 0);
		return thisInts;
	}

}
