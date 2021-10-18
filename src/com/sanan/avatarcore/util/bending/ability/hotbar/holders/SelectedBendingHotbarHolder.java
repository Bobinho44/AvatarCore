package com.sanan.avatarcore.util.bending.ability.hotbar.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;

public class SelectedBendingHotbarHolder implements InventoryHolder {

	private BendingHotbar selected;
	
	public SelectedBendingHotbarHolder(BendingHotbar hotbar) {
		super();
		this.selected = hotbar;
	}
	
	public BendingHotbar getSelected() {
		return selected;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}
	
	
}
