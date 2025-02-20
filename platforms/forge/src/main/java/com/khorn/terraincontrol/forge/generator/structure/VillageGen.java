package com.khorn.terraincontrol.forge.generator.structure;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.configuration.BiomeConfig.VillageType;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.forge.ForgeBiome;
import com.khorn.terraincontrol.util.minecraftTypes.StructureNames;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VillageGen extends MapGenStructure {
	/**
	 * A list of all the biomes villages can spawn in.
	 */
	public List<BiomeGenBase> villageSpawnBiomes;

	/**
	 * Village size, 0 for normal, 1 for flat map
	 */
	private int size;
	private int distance;
	private int minimumDistance;

	public VillageGen(WorldSettings configs) {
		size = configs.worldConfig.villageSize;
		distance = configs.worldConfig.villageDistance;
		minimumDistance = 8;

		// Add all village biomes to the list
		villageSpawnBiomes = new ArrayList<BiomeGenBase>();
		for (LocalBiome biome : configs.biomes) {
			if (biome == null)
				continue;
			if (biome.getBiomeConfig().villageType != VillageType.disabled) { villageSpawnBiomes.add(((ForgeBiome) biome).getHandle()); }
		}
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		int var3 = chunkX;
		int var4 = chunkZ;

		if (chunkX < 0) { chunkX -= this.distance - 1; }

		if (chunkZ < 0) { chunkZ -= this.distance - 1; }

		int var5 = chunkX / this.distance;
		int var6 = chunkZ / this.distance;
		Random var7 = this.worldObj.setRandomSeed(var5, var6, 10387312);
		var5 *= this.distance;
		var6 *= this.distance;
		var5 += var7.nextInt(this.distance - this.minimumDistance);
		var6 += var7.nextInt(this.distance - this.minimumDistance);

		if (var3 == var5 && var4 == var6) {
			boolean canSpawn = this.worldObj.getWorldChunkManager().areBiomesViable(var3 * 16 + 8, var4 * 16 + 8, 0, villageSpawnBiomes);

			if (canSpawn) { return true; }
		}

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		return new VillageStart(this.worldObj, this.rand, chunkX, chunkZ, this.size);
	}

	@Override
	public String func_143025_a() {
		return StructureNames.VILLAGE;
	}
}
