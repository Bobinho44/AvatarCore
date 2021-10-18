package com.sanan.avatarcore.util.bending.ability.hotbar.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;

public class HotbarChooseElementHolder implements InventoryHolder {

	private BendingHotbar hotbar;
	private int slot;
	
	public HotbarChooseElementHolder(BendingHotbar hotbar, int slot) {
		super();
		this.hotbar = hotbar;
		this.slot = slot;
	}
	
	public BendingHotbar getHotbar() {
		return hotbar;
	}
	
	public int getSlot() {
		return slot;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

	
	
}
