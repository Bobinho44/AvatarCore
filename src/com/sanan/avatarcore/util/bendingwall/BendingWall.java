package com.sanan.avatarcore.util.bendingwall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.movingblock.wallMovingBlock.WallMovingBlock;
import com.sanan.avatarcore.util.movingblock.wallMovingBlock.WallMovingBlockEntity;

public abstract class BendingWall {
	
	private Map<Location, Material> blocks = new LinkedHashMap<Location, Material>();
	private float health;
	private Location startingLocation;
	private WallMovingBlock wallLife;
	private long cooldown;
	
	public BendingWall(Location startingLocation, float health) {
		this.health = health;
		this.startingLocation = startingLocation;
		this.cooldown = 0;
	}
	
	public abstract void createWall(int heigh, int width);
	
	public abstract void destroyWall();
	
	public boolean mustDestroy() {
		return (getCooldown() != 0 && System.currentTimeMillis() - getCooldown() >= 10000) || getHealth() <= 0;
	}
	
	public void damageWall(float damage) {
		this.health -= damage;
		if (wallLife == null) {
   		 	setWallLife(new WallMovingBlock(new ItemBuilder(Material.AIR), new WallMovingBlockEntity(getCenter(), 20)));
		}
		this.wallLife.setLife(this.health);
	}
	
	public float getHealth() {
		return this.health;
	}
	
	public Map<Location, Material> getBlocks() {
		return this.blocks;
	}
	
	public void addBlock(Location location, Material material) {
		blocks.put(location, material);
	}
	
	public void removeBlock(Location location) {
		blocks.remove(location);
	}
	
	public Location getStartingLocation() {
		return this.startingLocation;
	}
	
	public void setStartingLocation(Location startingLocation) {
		this.startingLocation = startingLocation;
	}
	
	public void setWallLife(WallMovingBlock wallLife) {
		this.wallLife = wallLife;
	}
	
	public WallMovingBlock getWallLife() {
		return this.wallLife;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BendingWall)) return false;
		BendingWall wall = (BendingWall) object;
		return getStartingLocation().equals(wall.getStartingLocation()) && getBlocks().equals(wall.getBlocks());
	}
	
	public static ArrayList<Location> createStableRow(Location wallLocation, Location playerLocation, int halfWidth) {
		Vector yAxis = new Vector(0,-1,0);
	    Vector pos = playerLocation.getDirection().setY(0).normalize().crossProduct(yAxis.clone());
	    Vector neg = yAxis.clone().crossProduct(playerLocation.getDirection().setY(0).normalize());
	    Location posDir = wallLocation.clone();
	    Location negDir = wallLocation.clone();
	    ArrayList<Location> locations = new ArrayList<>();
	    locations.add(getBlockLocation(wallLocation));
	    for (int i = 0; i < halfWidth; i++) {
	    	posDir.add(Math.round(pos.getX()),0,Math.round(pos.getZ()));
	        negDir.add(Math.round(neg.getX()),0,Math.round(neg.getZ()));
	        locations.add(getBlockLocation(posDir));
	        locations.add(getBlockLocation(negDir));
	    }
	    return locations;
	}

	private static Location getBlockLocation(Location location) {
		Location newLocation = location.toBlockLocation();
		newLocation.setYaw(0);
		newLocation.setPitch(0);
		return newLocation;
	}
	
	private Location getCenter() {
		double maxY = blocks.keySet().iterator().next().toCenterLocation().getY();
		double maxX = blocks.keySet().iterator().next().toCenterLocation().getX();
		double maxZ = blocks.keySet().iterator().next().toCenterLocation().getZ();
		double minX = maxX;
		double minZ = maxZ;
		for (Location map : blocks.keySet()) {
			Location loc = map.toCenterLocation();
			maxY = loc.getY() > maxY ? loc.getY() : maxY;
			maxX = loc.getX() > maxX ? loc.getX() : maxX;
			maxZ = loc.getZ() > maxZ ? loc.getZ() : maxZ;
			minX = loc.getX() < minX ? loc.getX() : minX;
			minZ = loc.getZ() < minZ ? loc.getZ() : minZ;
		}
		
		return new Location(startingLocation.getWorld(), (maxX + minX) / 2, maxY + 1.5, (maxZ + minZ) / 2);
	}
}
