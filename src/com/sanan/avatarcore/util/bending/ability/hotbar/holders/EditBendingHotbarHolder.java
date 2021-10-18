package com.sanan.avatarcore.util.bending.ability.hotbar.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;

public class EditBendingHotbarHolder implements InventoryHolder {
	
	private BendingHotbar hotbar;
	
	public EditBendingHotbarHolder(BendingHotbar hotbar) {
		super();
		this.hotbar = hotbar;
	}
	
	public BendingHotbar getHotbar() {
		return hotbar;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

	
}
