package com.sanan.avatarcore.util.movingblock.EarthMovingBlock;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.movingblock.MovingBlock;
import com.sanan.avatarcore.util.movingblock.MovingBlockStructure;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthLittleChunk extends MovingBlockStructure {

	private ArrayList<Integer> block;
	
	public EarthLittleChunk(ArrayList<Material> blocks, BendingPlayer player, Location spawnLocation, boolean evolved) {
		super();
		block = evolved ? new ArrayList<Integer>(Arrays.asList(0,2,4,6,8,10,12,14,16,18,20,22,24,26)) : new ArrayList<Integer>(Arrays.asList(1,3,5,7,9,11,13,15,17,19,21,23,25));
		spawnLocation = spawnLocation.add(-0.62, -0.62, -0.62);
		for (int i = 0; i < 27; i++) {
			if (block.contains(i) && blocks.get(i) != Material.AIR){
				int[] coord = to3D(i);
				Location loc = spawnLocation.clone().add(coord[0]*0.62, coord[1]*0.62, coord[2]*0.62);
				MovingBlock movingBlock = new EarthMovingBlock(new ItemBuilder(blocks.get(i)), new EarthMovingBlockEntity(loc, player));
				addMovingBlockEntity(movingBlock);
			}
		}

	}

	private int[] to3D( int idx ) {
	    final int z = idx / (9);
	    idx -= (z * 9);
	    final int y = idx / 3;
	    final int x = idx % 3;
	    return new int[]{ x, y, z };
	}
	
	@Override
	public void translate(Location playerLoc) {
		Vector vector = playerLoc.clone().getDirection().multiply(6);
		Location loc = (playerLoc.clone().add(vector)).add(-0.62, -0.62, -0.62);
		int j = 0;
		for (int i = 0; i < 27 && j < getStructure().size(); i++) {
			if (block.contains(i) && getStructure().get(j).getBlock().getType() != Material.AIR){
				int[] coord = to3D(i);
				Location location = loc.clone().add(coord[0]*0.62, coord[1]*0.62, coord[2]*0.62);
				Chunk chunk = location.getChunk();
				chunk.load(true);
				getStructure().get(j).teleport(location);
				j++;
			}
		}
	}
	
}
