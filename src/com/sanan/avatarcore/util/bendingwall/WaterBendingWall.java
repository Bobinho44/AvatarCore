package com.sanan.avatarcore.util.bendingwall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;

public class WaterBendingWall extends BendingWall {
	
	private final AvatarCore ac = AvatarCore.getInstance();
	private final WallManager wm = WallManager.getInstance();

    private boolean isSolid;
    private List<Boolean> haveColumn = new ArrayList<Boolean>();
    private Location direction;
    private Player owner;
    
	public WaterBendingWall(Player player, Location startingLocation) {
		super(startingLocation, 20);
		this.owner = player;
		this.isSolid = false;
		this.direction = player.getLocation();
		for (Location location : BendingWall.createStableRow(startingLocation, player.getLocation(), 3)) {
    		haveColumn.add(location.clone().add(0, -1, 0).getBlock().getType() == Material.WATER);
		}
	}

	@Override
	public void createWall(int heigh, int distance) {
        int j = 0;
       	for (Location location : BendingWall.createStableRow(getStartingLocation(), direction, 3)) {
       		for (int i = 0; i < heigh; i++) {
       			Location loc = location.clone().add(0, i, 0);
	       		if (haveColumn.get(j % 7)) {
	       			Material oldType = loc.getBlock().getType();
	       			BendingWall wall = wm.isFromWall(loc);
			         if (wall != null) 
				         	oldType = wall.getBlocks().get(loc);
			         addBlock(loc, oldType);
			         if (isSolid) 
			        	 loc.getBlock().setType(Material.ICE);
			         else if (wall == null){
			        	 Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(loc, Material.WATER.createBlockData()));
			         }
	        	}
       		} 
       		j++;
        }
        if (isSolid) {
        	wm.createWall(this);
        }
	}

	@Override
	public void destroyWall() {
		if (getWallLife() != null)
			getWallLife().remove();
		ArrayList<Item> dropedBlocks = new ArrayList<Item>();
		for (Entry<Location, Material> map : new LinkedHashMap<Location, Material>(getBlocks()).entrySet()) {
			Location location = map.getKey().clone();
		    Material material = map.getValue();
		    if (!location.getBlock().getType().equals(Material.AIR) && isSolid) {
		    	Item dropedBlock = location.getWorld().dropItemNaturally(location.clone().add(0, 1, 0), new ItemStack(location.getBlock().getType()));
				dropedBlock.setPickupDelay(999999999);
				dropedBlocks.add(dropedBlock);  
		    }
		 	removeBlock(map.getKey());
		 	if (wm.isFromWall(location) == null) {
		 		if (isSolid)
		 			location.getBlock().setType(material);
		 		else {
		 			Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, location.getBlock().getBlockData()));
		 		}
		 	}
		}
		new BukkitRunnable() {
			public void run() {
				for (Item dropedBlock : dropedBlocks) {
					dropedBlock.remove();
				}
			}
		}.runTaskLaterAsynchronously(ac, 60);
		if (isSolid)
			wm.destroyWall(WaterBendingWall.this);
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void solidification(int shootDistance) {
		destroyWall();
		setCooldown(System.currentTimeMillis());
		this.isSolid = true;
		createWall(5, shootDistance);	
	}
	
	public boolean isSolid() {
		return this.isSolid;
	}
	
	public void setDirection(Location direction) {
		this.direction = direction;
	}
	
	public List<LivingEntity> entityColision() {
		List<LivingEntity> victims = new ArrayList<LivingEntity>();
		for (Location location : getBlocks().keySet()) {
			for (LivingEntity entity : location.getNearbyLivingEntities(1)) {
				victims.add(entity);
			}
		}
		return victims;
	}
	
}
