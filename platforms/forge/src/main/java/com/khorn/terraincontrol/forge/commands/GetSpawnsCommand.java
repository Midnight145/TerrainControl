package com.khorn.terraincontrol.forge.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class GetSpawnsCommand extends CommandBase {

	@Override
	public String getCommandName() { // TODO Auto-generated method stub
		return "getspawns";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) { // TODO Auto-generated method stub
		return "getspawns";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arg1) { // TODO Auto-generated method stub
		final EntityPlayerMP player = (EntityPlayerMP) sender;
		final World world = player.worldObj;
		final BiomeGenBase currentBiome = world.getBiomeGenForCoords((int) player.posX, (int) player.posZ);
		final ArrayList<String> str = new ArrayList<String>();
		str.add("Ambient: ");
		System.out.print("Ambient: ");
		for (final Object obj : currentBiome.getSpawnableList(EnumCreatureType.ambient)) {
			final SpawnListEntry entry = (SpawnListEntry) obj;
			str.add(entry.entityClass.getName() + ", ");
			System.out.print(entry.entityClass.getName());

		}
		str.add("\n");
		System.out.println();

		System.out.print("Creature: ");
		for (final Object obj : currentBiome.getSpawnableList(EnumCreatureType.creature)) {
			final SpawnListEntry entry = (SpawnListEntry) obj;
			str.add(entry.entityClass.getName() + ", ");
			System.out.print(entry.entityClass.getName());
		}
		str.add("\n");
		System.out.println();

		System.out.print("WaterCreature: ");
		for (final Object obj : currentBiome.getSpawnableList(EnumCreatureType.waterCreature)) {
			final SpawnListEntry entry = (SpawnListEntry) obj;
			str.add(entry.entityClass.getName() + ", ");
			System.out.print(entry.entityClass.getName());

		}
		str.add("\n");
		System.out.println();
		System.out.print("Monster: ");
		for (final Object obj : currentBiome.getSpawnableList(EnumCreatureType.monster)) {
			final SpawnListEntry entry = (SpawnListEntry) obj;
			str.add(entry.entityClass.getName() + ", ");
			System.out.print(entry.entityClass.getName());

		}
		sender.addChatMessage(new ChatComponentText(String.join("", str)));
	}
	
}
