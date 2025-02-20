package com.khorn.terraincontrol.forge;

import java.io.File;

import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.forge.generator.ForgeVanillaBiomeGenerator;
import com.khorn.terraincontrol.forge.generator.TCWorldChunkManager;
import com.khorn.terraincontrol.forge.util.WorldHelper;
import com.khorn.terraincontrol.generator.biome.BiomeGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class TCWorldType extends WorldType {
	public ForgeWorld worldTC;

	public TCWorldType(String paramString) {
		super(paramString);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showWorldInfoNotice() {
		return true;
	}

	// Actually: getBiomeManager
	@Override
	public WorldChunkManager getChunkManager(World world) {
		try {
			if (world instanceof WorldClient) { return super.getChunkManager(world); }
		} catch (NoClassDefFoundError e) {
			// There isn't a WorldClient class, so we are on a stand-alone
			// server. Continue normally.
		}
		// Restore old biomes
		ForgeWorld.restoreBiomes();

		// Load everything
		File worldDirectory = new File(world.getSaveHandler().getWorldDirectory(), "TerrainControl");

		if (!worldDirectory.exists()) {
			System.out.println("TerrainControl: settings does not exist, creating defaults");

			if (!worldDirectory.mkdirs()) { System.out.println("TerrainControl: cant create folder " + worldDirectory.getAbsolutePath()); }
		}

		this.worldTC = new ForgeWorld(world.getSaveHandler().getWorldDirectoryName());
		WorldSettings config = new WorldSettings(worldDirectory, this.worldTC, false);
		this.worldTC.Init(world, config);

		Class<? extends BiomeGenerator> biomeGenClass = this.worldTC.getConfigs().getWorldConfig().biomeMode;
		BiomeGenerator biomeManager = TerrainControl.getBiomeModeManager().createCached(biomeGenClass, this.worldTC);
		WorldChunkManager chunkManager = this.createWorldChunkManager(this.worldTC, biomeManager);
		this.worldTC.setBiomeManager(biomeManager);

		return chunkManager;
	}

	/**
	 * Gets the appropriate WorldChunkManager. For the vanilla biome
	 * generator we have to use WorldChunkManager, for other biome modes
	 * TCWorldChunkManager is the right option.
	 *
	 * @param world          ForgeWorld instance, needed to instantiate the
	 *                       WorldChunkManager.
	 * @param biomeGenerator Biome generator.
	 * @return The most appropriate WorldChunkManager.
	 */
	private WorldChunkManager createWorldChunkManager(ForgeWorld world, BiomeGenerator biomeGenerator) {
		if (biomeGenerator instanceof ForgeVanillaBiomeGenerator) {
			WorldChunkManager worldChunkManager = super.getChunkManager(world.getWorld());
			((ForgeVanillaBiomeGenerator) biomeGenerator).setWorldChunkManager(worldChunkManager);
			return worldChunkManager;
		}
		else {
			return new TCWorldChunkManager(this.worldTC, biomeGenerator);
		}
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		if (this.worldTC.getConfigs().getWorldConfig().ModeTerrain != WorldConfig.TerrainMode.Default) {
			return this.worldTC.getChunkGenerator();
		}
		else {
			return super.getChunkGenerator(world, generatorOptions);
		}
	}

	@Override
	public int getMinimumSpawnHeight(World mcWorld) {
		LocalWorld world = WorldHelper.toLocalWorld(mcWorld);
		if (world == null) {
			// MCPC+ has an interesting load order sometimes
			return 64;
		}
		return world.getConfigs().getWorldConfig().waterLevelMax;
	}
}
