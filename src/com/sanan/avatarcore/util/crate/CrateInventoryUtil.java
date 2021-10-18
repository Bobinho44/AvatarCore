package com.sanan.avatarcore.util.crate;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.ChatColor;

public class CrateInventoryUtil {

	private final static BendingCrateManager bcm = BendingCrateManager.getInstance();
	
	public static Inventory getCratePlayMenu(BendingCrate crate, String type) {
		String color = ChatColor.getLastColors(CrateItemUtil.getCrateShard(type).getItemMeta().getDisplayName());
		Inventory inv = Bukkit.createInventory(new CratePlayMenu(crate), 54, color + type + "'s Crate");
	
		ItemStack blackDivider = CrateItemUtil.getCrateMenuDivider(Material.BLACK_STAINED_GLASS_PANE);
		ItemStack grayDivider = CrateItemUtil.getCrateMenuDivider(Material.GRAY_STAINED_GLASS_PANE);
		ItemStack greenDivider = CrateItemUtil.getCrateMenuDivider(Material.GREEN_STAINED_GLASS_PANE);
		for (int i = 0; i < 54; i++) {
			if ((i % 9) % 2 == 0) {
				inv.setItem(i, blackDivider);
			}
			else {
				inv.setItem(i, grayDivider);
			}
		}
		int[] greenDividerIndex = {19, 21, 23, 25};
		for (int index : greenDividerIndex) {
			inv.setItem(index, greenDivider);
		}
		
		int[] shardSlot = {47, 49, 51};
		for (int index : shardSlot) {
			inv.setItem(index, new ItemStack(Material.AIR));
		}
		for (int j = 1; j <= 3; j++ ) {
			setRound(crate, inv, j);
		}
		inv.setItem(53, CrateItemUtil.getInformationBook());
		return inv;
	}
	
	public static Inventory getCrateCreateListMenu(String type) {
		String color = ChatColor.getLastColors(CrateItemUtil.getCrateShard(type).getItemMeta().getDisplayName());
		Inventory inv = Bukkit.createInventory(new CrateCreateListMenu(), 54, color + "Create " + type + "'s Crate");
		int i = 0;
		for (Entry<ItemStack, Double> map : bcm.getItemsCollection(type).entrySet()) {
			inv.setItem(i, bcm.getInformationItem(map.getKey(), map.getValue()));
			i++;
		}
		return inv;
	}
	
	public static void setRound(BendingCrate crate, Inventory inv, int roundNumber) {
		List<ItemStack> items = crate.getItems(roundNumber);
		for (int i = 0; i < 5; i++) {
			inv.setItem(2 + 2 * (roundNumber - 1) + 9 * i, items.get(i));
		}
	}
	
}