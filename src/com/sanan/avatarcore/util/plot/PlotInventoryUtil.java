package com.sanan.avatarcore.util.plot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.nation.BendingNation;

import net.md_5.bungee.api.ChatColor;

public class PlotInventoryUtil {

	public static Inventory getPlotShopMenu(BendingNation nation, Player player) {
		String nationElement = nation.getName();
		Inventory inv = Bukkit.createInventory(new PlotShopMenu(nation), 54, nationElement + " Realtor");
		ItemStack divider = PlotItemUtil.getPlotMenuDivider();
		for (int i=45; i<54; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(49, PlotItemUtil.getPlotMenuPlayerInfo(player));		
		for (Plot plot : PlotManager.getInstance().getAllNationPlot(ChatColor.stripColor(nationElement))) {
			inv.addItem(PlotItemUtil.getPlotItem(plot));
		}
		return inv;
	}
	
}
