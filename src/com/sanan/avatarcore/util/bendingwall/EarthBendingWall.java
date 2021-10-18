package com.sanan.avatarcore.util.bendingwall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingBlock;

public class EarthBendingWall extends BendingWall {
	
	private final AvatarCore ac = AvatarCore.getInstance();
	private final WallManager wm = WallManager.getInstance();
	
    private Location playerLocation;
    
	public EarthBendingWall(Location startingLocation, Location playerLocation, int heigh) {
		super(startingLocation, 20);
		this.playerLocation = playerLocation;
		createWall(heigh, 7);
	}

	@Override
	public void createWall(int heigh, int width) {
        new BukkitRunnable() {
        	int i = 0;
        	List<BendingWall> otherWalls = new ArrayList<BendingWall>();
		    public void run() {
		    	wm.createWall(EarthBendingWall.this);
		    	 if (i < heigh) {
		         	for (Location location : BendingWall.createStableRow(getStartingLocation().clone().add(0, i + 1, 0), playerLocation, 3)) {
		         		if (i == 0) {
		         			Location bottomLocation = location.clone();
		         			BendingWall bottomWall = wm.isFromWall(bottomLocation);
		         			if (bottomWall == null) {
		         				bottomWall = wm.isFromWall(bottomLocation.add(0, -1, 0));
		         			}
		         			while (bottomWall != null) {
		         				otherWalls.add(bottomWall);
				         		addBlock(bottomLocation.clone(), bottomWall.getBlocks().get(bottomLocation));
				         		bottomWall.removeBlock(bottomLocation);
				         		bottomWall = wm.isFromWall(bottomLocation.add(0, -1, 0));
		         			}
		         		}
		         		Material oldType = location.getBlock().getType();
		         		BendingWall wall = wm.isFromWall(location);
		         		if (wall != null) {
		         			oldType = wall.getBlocks().get(location);
		         		}
		         		addBlock(location, oldType);
		         		for (int j = 0; j < i; j++) {
		         			Material selectedType = location.clone().add(0, -(j+1), 0).getBlock().getType();
			         		location.clone().add(0, -j, 0).getBlock().setType(BendingBlock.isEarthBendingBlock(selectedType) ? selectedType : Material.DIRT);
		         		}
		         		Material selectedType = location.clone().add(0, -(2*i+1), 0).getBlock().getType();
		         		location.clone().add(0, -i, 0).getBlock().setType(BendingBlock.isEarthBendingBlock(selectedType) ? selectedType : Material.DIRT);
		         	}
		         	i++;
		    	 } 
		    	 else {
		    		 for (BendingWall wall : otherWalls) {
		    			 if (wall.getBlocks().size() == 0) {
		    				 wm.destroyWall(wall);
		    			 }
		    		 }
		    		 setCooldown(System.currentTimeMillis());
		    		 cancel();
		         }
		    }
		}.runTaskTimer(ac, 0L, 10L);
		
	}

	@Override
	public void destroyWall() {
		if (getWallLife() != null)
			getWallLife().remove();
		ArrayList<Item> dropedBlocks = new ArrayList<Item>();
		for (Entry<Location, Material> map : new LinkedHashMap<Location, Material>(getBlocks()).entrySet()) {
			Location location = map.getKey().clone();
		    Material material = map.getValue();
		    if (!map.getKey().getBlock().getType().equals(Material.AIR)) {
		    	Item dropedBlock = location.getWorld().dropItemNaturally(location.clone().add(0, 1, 0), new ItemStack(location.getBlock().getType()));
				dropedBlock.setPickupDelay(999999999);
				dropedBlocks.add(dropedBlock);  
		    }
		 	removeBlock(map.getKey());
		 	if (wm.isFromWall(location) == null) {
		 		location.getBlock().setType(material);
		 	}
		}
		new BukkitRunnable() {
			public void run() {
				for (Item dropedBlock : dropedBlocks) {
					dropedBlock.remove();
				}
			}
		}.runTaskLaterAsynchronously(ac, 60);
		wm.destroyWall(EarthBendingWall.this);
	}

}
