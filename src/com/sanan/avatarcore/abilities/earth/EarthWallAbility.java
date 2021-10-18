package com.sanan.avatarcore.abilities.earth;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.bendingwall.BendingWall;
import com.sanan.avatarcore.util.bendingwall.EarthBendingWall;
import com.sanan.avatarcore.util.player.BendingPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EarthWallAbility extends EarthBendingAbility {
	
	private List<BendingWall> walls = new ArrayList<BendingWall>();
	private Location curr;
	
    public EarthWallAbility(BendingPlayer player, Location startingLocation) {
        super(player);
        this.curr = addNewWall(startingLocation);
		new BukkitRunnable() {
	        public void run() {
	        	start();
	        }
	    }.runTaskLaterAsynchronously(AvatarCore.getInstance(), 1);
    }
    
    @Override
    public void update() {
    	super.update();
    	if (walls.size() <= 0) {
    		remove();
    	}
    	else {
	    	for (BendingWall wall : new ArrayList<BendingWall>(walls)) {
	    		if (wall.mustDestroy()) {
	    			wall.destroyWall();
	    			walls.remove(wall);
	    		}
	    	}
    	}
    }
    
    public Location addNewWall(Location startingLocation) {
    	double initialY = startingLocation.getY();
        while (!isBottom(BendingWall.createStableRow(startingLocation, getPlayer().getLocation(), 3))) {
        	startingLocation.add(0, -1, 0);
        }
        if (initialY - startingLocation.getY() <= 3) {
	        BendingWall wall = new EarthBendingWall(startingLocation, getPlayer().getLocation(), (int) (initialY - startingLocation.getY() + 2));
	        walls.add(wall);
        }
        return startingLocation;
    }
    
    public int getWallNumber() {
    	return isStarted() ? walls.size() : 0;
    }

    public static boolean isBottom(ArrayList<Location> locations) {
    	for (Location location : locations) {
    		if (!location.getBlock().getType().isSolid()) {
    			return false;
    		}
    	}
    	return true;
    }
    
    @Override
    public String getName() {
        return "Earth Wall";
    }

    @Override
    public String getDescription() {
        return "Creates earth wall";
    }

    @Override
    public Location getLocation() {
        return curr;
    }

    @Override
    public Material getMaterial() {
        return Material.GRASS_BLOCK;
    }

    @Override
    public boolean isToggleAbility() {
        return false;
    }

    @Override
    public boolean isDamageAbility() {
        return false;
    }

    @Override
    public String getDamageString() {
        return "";
    }

    @Override
    public int getChiCost() {
        return 25;
    }
    
	@Override
	public int getLevelRequired() {
		return 10;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
