package com.sanan.avatarcore.util.plot;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import com.sanan.avatarcore.util.nation.BendingNation;

public class PlotShopMenu implements InventoryHolder {

	private BendingNation nation;
	
	public PlotShopMenu(BendingNation nation) {
		this.nation = nation;
	}
	
	public BendingNation getNation() {
		return nation;
	}
	
	@Override
	public @NotNull Inventory getInventory() {
		return null;
	}

}
