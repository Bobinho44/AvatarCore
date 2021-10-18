package com.sanan.avatarcore.util.movingblock;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;
import com.sanan.avatarcore.util.item.ItemBuilder;

public abstract class MovingBlock {

	private MovingBlockEntity entity;
	private ItemStack block;
	
	public MovingBlock(ItemBuilder block, MovingBlockEntity entity) {
		this.entity = entity;
		setBlock(block);
		setVisible(false);
		entity.getEntity().setMarker(true);
	}
	
	public void teleport(Location location) {
		Preconditions.checkNotNull(location, "location cannot be null");
		entity.teleport(location);
	}
		
	public void remove() {
		entity.remove();
	}
	
	public Location getLocation() {
		return entity.getLocation();
	}
	
	public String getName() {
		return entity.getName();
	}
	
	public ItemStack getBlock() {
		return block;
	}
	
	public MovingBlockEntity getMovingBlock() {
		return entity;
	}
	
	public void setBlock(ItemBuilder block) {
		Preconditions.checkNotNull(block, "block cannot be null");
		this.block = block.build();
		entity.setHelmet(this.block);
	}
	
	public void setName(String name) {
		Preconditions.checkNotNull(name, "name cannot be null");
		entity.setName(name);
	}
	
	public void cleanName() {
		entity.cleanName();
	}
	
	public void setVisible(boolean visible) {
		Preconditions.checkNotNull(visible, "visible cannot be null");
		entity.setVisible(visible);
	}
}
