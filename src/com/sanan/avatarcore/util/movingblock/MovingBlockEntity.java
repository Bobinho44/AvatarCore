package com.sanan.avatarcore.util.movingblock;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public abstract class MovingBlockEntity {
	
	private ArmorStand entity;
	
	public MovingBlockEntity(ArmorStand entity) {
		this.entity = entity;	
	}
	
	public ArmorStand getEntity() {
		return entity;
	}
	
	public void teleport(Location location) {
		entity.teleport(location);
	}
		
	public void remove() {
		entity.remove();
	}
	
	public Location getLocation() {
		return entity.getLocation();
	}
	
	public String getName() {
		return entity.getCustomName();
	}
	
	public MovingBlockEntity setHelmet(ItemStack item) {
		entity.getEquipment().setHelmet(item);
		return this;
	}
	
	public MovingBlockEntity setName(String name) {
		entity.setCustomNameVisible(true);
		entity.setCustomName(name);
		return this;
	}
	
	public MovingBlockEntity cleanName() {
		entity.setCustomNameVisible(false);
		entity.setCustomName(null);
		return this;
	}
	
	public MovingBlockEntity setVisible(boolean visible) {
		entity.setVisible(visible);
		return this;
	}
	
}
