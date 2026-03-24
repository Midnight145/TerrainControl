package com.khorn.terraincontrol.generator.biome;

import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.util.ChunkCoordinate;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Iterator;

import net.minecraft.world.ChunkCoordIntPair;

/**
 * Wraps uncached biome generators.
 *
 * @see BiomeModeManager#createCached(Class, LocalWorld)
 */
class CachedBiomeGenerator extends BiomeGenerator {
	/**
	 * Caches the biomes of a single chunk.
	 */
	private static class Block {
		private int[] biomes = new int[ChunkCoordinate.CHUNK_X_SIZE * ChunkCoordinate.CHUNK_Z_SIZE];
		private long lastAccessGeneration;

		Block(BiomeGenerator generator, int chunkX, int chunkZ) {
			biomes = generator.getBiomes(biomes, chunkX << 4, chunkZ << 4, ChunkCoordinate.CHUNK_X_SIZE,
					ChunkCoordinate.CHUNK_Z_SIZE, OutputType.DEFAULT_FOR_WORLD);
		}

		/**
		 * Gets the biome type id of the column at the given location.
		 * 
		 * @param blockX X location of the column, must fall in this cache
		 *               block.
		 * @param blockZ Z location of the column, must fall in this cache
		 *               block.
		 * @return The biome type id.
		 */
		int getCalculatedBiomeId(int blockX, int blockZ) {
			return biomes[blockX & 15 | (blockZ & 15) << 4];
		}
	}

	// Pre-size to avoid rehashing during typical worldgen (render distance worth of chunks)
	private final Long2ObjectOpenHashMap<CachedBiomeGenerator.Block> cacheMap = new Long2ObjectOpenHashMap<>(256);
	private final BiomeGenerator generator;
	private long generation;
	private static final long CLEANUP_INTERVAL = 128;
	private static final long EVICT_AGE = 512;

	private CachedBiomeGenerator(BiomeGenerator generator) {
		super(generator.world);
		this.generator = generator;
	}

	/**
	 * Gets a cached generator that generates biomes like the given generator.
	 * If the given generator is already cached, it is returned immediately.
	 * If it isn't cached it is wrapped inside a {@link CachedBiomeGenerator}.
	 * 
	 * @param generator A potentially uncached biome generator.
	 * @return A cached biome generator.
	 * @see BiomeModeManager#createCached(Class, LocalWorld)
	 */
	static BiomeGenerator makeCached(BiomeGenerator generator) {
		if (generator.isCached()) { return generator; }
		return new CachedBiomeGenerator(generator);
	}

	private static long packChunk(int chunkX, int chunkZ) {
		return ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
	}

	@Override
	public void cleanupCache() {
		generation++;
		if (generation % CLEANUP_INTERVAL != 0) { return; }

		Iterator<Long2ObjectMap.Entry<CachedBiomeGenerator.Block>> it = cacheMap.long2ObjectEntrySet().fastIterator();
		while (it.hasNext()) {
			Long2ObjectMap.Entry<CachedBiomeGenerator.Block> entry = it.next();
			long age = generation - entry.getValue().lastAccessGeneration;
			if (age > EVICT_AGE) { it.remove(); }
		}
	}

	@Override
	public int getBiome(int x, int z) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		CachedBiomeGenerator.Block cacheBlock = getBiomeCacheBlock(chunkX, chunkZ);
		return cacheBlock.getCalculatedBiomeId(x, z);
	}

	private CachedBiomeGenerator.Block getBiomeCacheBlock(int chunkX, int chunkZ) {
		long key = packChunk(chunkX, chunkZ);
		CachedBiomeGenerator.Block block = this.cacheMap.get(key);

		if (block == null) {
			block = new CachedBiomeGenerator.Block(generator, chunkX, chunkZ);
			this.cacheMap.put(key, block);
		}

		block.lastAccessGeneration = generation;
		return block;
	}

	@Override
	public int[] getBiomes(int[] biomeArray, int x, int z, int xSize, int zSize, OutputType type) {
		if (xSize == ChunkCoordinate.CHUNK_X_SIZE && zSize == ChunkCoordinate.CHUNK_Z_SIZE && (x & 0xF) == 0 && (z & 0xF) == 0) {
			if (biomeArray == null || biomeArray.length < xSize * zSize) { biomeArray = new int[xSize * zSize]; }
			int[] cachedBiomes = getCachedBiomes(x >> 4, z >> 4);
			System.arraycopy(cachedBiomes, 0, biomeArray, 0, xSize * zSize);
			return biomeArray;
		}
		return generator.getBiomes(biomeArray, x, z, xSize, zSize, type);
	}

	@Override
	public int[] getBiomesUnZoomed(int[] biomeArray, int x, int z, int xSize, int zSize, OutputType type) {
		return generator.getBiomesUnZoomed(biomeArray, x, z, xSize, zSize, type);
	}

	@Override
	public boolean canGenerateUnZoomed() {
		return generator.canGenerateUnZoomed();
	}

	/**
	 * Returns the array of cached biome types in the BiomeCacheBlock at the
	 * given location.
	 */
	public int[] getCachedBiomes(ChunkCoordinate chunkCoord) {
		return getCachedBiomes(chunkCoord.getChunkX(), chunkCoord.getChunkZ());
	}

	private int[] getCachedBiomes(int chunkX, int chunkZ) {
		return this.getBiomeCacheBlock(chunkX, chunkZ).biomes;
	}

	@Override
	public float[] getRainfall(float[] paramArrayOfFloat, int x, int z, int xSize, int zSize) {
		return generator.getRainfall(paramArrayOfFloat, x, z, xSize, zSize);
	}

	@Override
	public boolean isCached() { return true; }

	@Override
	public BiomeGenerator unwrap() {
		return generator.unwrap();
	}
}
