package com.sanan.avatarcore.abilities.water;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.bendingwall.WaterBendingWall;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class ShieldOfWaterAbility extends WaterBendingAbility implements EnduringAbility {
	
	private int actualTask;
	private WaterBendingWall wall;
	private int shootDistance = 0;
	private Location center;
	private double initialDistance;
	
	public ShieldOfWaterAbility(BendingPlayer player, Location startLocation) {
		super(player);
		this.actualTask = 0;
		this.initialDistance = Math.sqrt(Math.pow(getPlayer().getLocation().getX() - startLocation.getX(), 2) + Math.pow(getPlayer().getLocation().getZ() - startLocation.getZ(), 2));
		this.wall = new WaterBendingWall(getPlayer(), startLocation);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		if (actualTask == 0) {
			actualTask = 1;
		}
		else if (wall.isSolid()) {
			stop();
			if (wall.mustDestroy() || getPlayer() == null || getPlayer().isDead() || !getPlayer().isOnline()) {
	    		remove();
	    	}
		}
		else if (actualTask != 3) {
			center = getPlayer().getLocation();
			wall.destroyWall();
			wall.setDirection(center);
			Location wallLoc = getWallLocation();
			if (wallLoc != null) {
				wall.setStartingLocation(getWallLocation());
			}
			wall.createWall(5, 0);	
		}
		else {
			shootDistance++;
	    	wall.setStartingLocation(getWallLocation());
	    	if (wall.isSolid() || shootDistance >= 25) {
	    		if (shootDistance >= 25) {
	    			remove();
	    		}

	    	}
	    	else {
		    	wall.destroyWall();
				//less than 20 blocks
				if (shootDistance <= 20) {
					wall.createWall(5, shootDistance);
				}
				//more than 20 blocks (block go down)
				else {
					wall.createWall(25 - shootDistance, shootDistance);
				}
					
				//Hurted player
				for (LivingEntity entity : wall.entityColision()) {
					bendingDamage(entity, 6, 4);
					entity.setVelocity(entity.getLocation().getDirection().multiply(-2));
				}
	    	}
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		if (isSolid()) {
			wall.destroyWall();
		}
		else {
			new BukkitRunnable() {
		        public void run() {
		        	wall.destroyWall();
		        }
		    }.runTaskLaterAsynchronously(AvatarCore.getInstance(), 1);
		}
	}
	
	public void shoot() {
		actualTask = 3;
		center = getPlayer().getLocation();
	}
	
	public int getActualTask() {
		return actualTask;
	}
	
	public void solidification() {
		wall.solidification(shootDistance);
	}
	
	public boolean isSolid() {
		return wall.isSolid();
	}
	
	private Location getWallLocation() {
		Location playerLoc = getPlayer().getLocation();
		Location wallLoc = wall.getStartingLocation();
		double distance = Math.sqrt(Math.pow(playerLoc.getX() - wallLoc.getX(), 2) + Math.pow(playerLoc.getZ() - wallLoc.getZ(), 2));
		if (distance > 2.75 || actualTask >= 2) {
			if (actualTask == 1) {
				actualTask = 2;
			}
			distance = 3;
		}
		else if (distance <= initialDistance) {
			distance = initialDistance;
		}
		double yaw = Math.toRadians(center.getYaw()) + Math.PI/2;
		distance += shootDistance;
    	Location loc = center.clone().add(Math.round(Math.cos(yaw)) * distance, 0, Math.round(Math.sin(yaw)) * distance);
    	while (loc.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
    		loc.add(0, -1, 0);
    	}
    	return loc;
	}
	
	public WaterBendingWall getWall() {
		return wall;
	}
	
	@Override
	public String getName() {
		return "Shield Of Water";
	}

	@Override
	public String getDescription() {
		return "Fire shield.";
	}

	@Override
	public Location getLocation() {
		return wall.getStartingLocation().clone();
	}

	@Override
	public Material getMaterial() {
		return Material.SHIELD;
	}

	@Override
	public boolean isToggleAbility() {
		return false;
	}

	@Override
	public boolean isDamageAbility() {
		return true;
	}

	@Override
	public String getDamageString() {
		return "6";
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
