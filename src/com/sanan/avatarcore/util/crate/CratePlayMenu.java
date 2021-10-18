package com.sanan.avatarcore.util.crate;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CratePlayMenu implements InventoryHolder {

	private BendingCrate crate;
	
	public CratePlayMenu(BendingCrate crate) {
		this.crate = crate;
	}
	
	public BendingCrate getCrate() {
		return crate;
	}
	
	@Override
	public @NotNull Inventory getInventory() {
		return null;
	}

}
