package com.sanan.avatarcore.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.crate.BendingCrate;
import com.sanan.avatarcore.util.crate.BendingCrateManager;
import com.sanan.avatarcore.util.crate.CrateCreateListMenu;
import com.sanan.avatarcore.util.crate.CrateItemUtil;
import com.sanan.avatarcore.util.crate.CratePlayMenu;
import com.sanan.avatarcore.util.player.PlayerManager;

public class CrateListener implements Listener {

	private final BendingCrateManager bcm = BendingCrateManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	
	@EventHandler
	public void onOpenPlayCrateMenu(PlayerInteractEvent event) {
		if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.PRISMARINE_SHARD) {
			String type = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().replace("[", "").replace("]", ""));
			if (bcm.getItemsCollection(type).size() == 0) return;
			new BendingCrate(event.getPlayer(), type);
		}
	}
	
	@EventHandler
	public void onCloseCreateCrateMenu(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof CratePlayMenu) {
			BendingCrate crate = ((CratePlayMenu) event.getInventory().getHolder()).getCrate();
			if (crate.isStarted()) {
				crate.stop();
			}
			int[] shardIndex = {47, 49, 51};
			for (int index : shardIndex) {
				ItemStack item = event.getInventory().getItem(index);
				if (item != null) {
					pm.give((Player) event.getPlayer(), Arrays.asList(item));
				}
			}
		}
		else if (event.getInventory().getHolder() instanceof CrateCreateListMenu) {
			Map<ItemStack, Double> collection = new HashMap<ItemStack, Double>();
	    	for (int i = 0; i < 54; i++) {
				if (event.getInventory().getItem(i) != null) {
					ItemStack item = event.getInventory().getItem(i);
					collection.put(bcm.getNormalItem(item), bcm.getPercent(item));
				}
	    	}
			String type = ChatColor.stripColor(event.getView().getTitle().replace("'s Crate", "").replace("Create ", ""));
			bcm.setNewCollection(collection, type);
		}
	}
	
	@EventHandler
	public void onClick(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof CrateCreateListMenu || event.getInventory().getHolder() instanceof CratePlayMenu) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		
		ItemStack curr = event.getCurrentItem();
		ItemStack curs = event.getCursor();
		if (event.getInventory().getHolder() instanceof CrateCreateListMenu) {
			if (event.getClickedInventory().getType() == InventoryType.PLAYER && !event.getClick().isShiftClick()) return;
			event.setCancelled(true);
			if (event.getClickedInventory().getType() == InventoryType.PLAYER && event.getClick().isShiftClick()) return;
			if (event.getClick().isShiftClick() && curr != null) {
				double percent = bcm.getPercent(curr);
				ClickType click = event.getClick();
				if ((percent == 0.1 && click.isRightClick()) || (percent == 1.0 && click.isLeftClick()) || (percent < 1 && percent > 0.1)) {
					percent += ((event.getClick().isRightClick()) ? 0.1 : -0.1);
				}
				else if ((percent == 100.0 && click.isLeftClick()) || (percent == 1.0 && click.isRightClick()) || (percent > 1.0 && percent < 100.0)) {
					percent += ((event.getClick().isRightClick()) ? 1 : -1);
				}
				event.setCurrentItem(bcm.getInformationItem(curr, Math.round(percent * 10.0) / 10.0));
			}
			else if (onTake(event)) {
				event.getWhoClicked().setItemOnCursor(bcm.getNormalItem(curr));
				event.setCurrentItem(new ItemStack(Material.AIR));
			}
			else if (onPlace(event)) {
				event.setCurrentItem(bcm.getInformationItem(curs, 50));
				event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
			}
		}
		else if (event.getInventory().getHolder() instanceof CratePlayMenu) {
			if (((CratePlayMenu) event.getInventory().getHolder()).getCrate().isStarted()) {
				event.setCancelled(true);
				return;
			}
			if (event.getClickedInventory().getType() == InventoryType.PLAYER && (!event.getClick().isShiftClick() && !(onTake(event) && curr.getItemMeta().hasLore() && ChatColor.stripColor(curr.getItemMeta().getLore().toString()).contains("This shard")))) return;
			event.setCancelled(true);
			if (event.getClick() == ClickType.LEFT && curr != null && curr.isSimilar(CrateItemUtil.getSpinner())) {
				((CratePlayMenu) event.getClickedInventory().getHolder()).getCrate().animation();
			}
			else if (canTakeShard(event)) {
				event.getWhoClicked().setItemOnCursor(curr.clone());
				event.setCurrentItem(new ItemStack(Material.AIR));
				if (event.getSlot() == 47) {
					event.getClickedInventory().setItem(26, CrateItemUtil.getCrateMenuDivider(Material.BLACK_STAINED_GLASS_PANE));
				}
			}
			else if (canPlaceShard(event)) {
				event.getInventory().setItem(26, CrateItemUtil.getSpinner());
			}
		}
	}
	 
	private boolean canPlaceShard(InventoryClickEvent event) {
		String type = ChatColor.stripColor(event.getView().getTitle().replace("'s Crate", "").replace("[", "")).replace("]", "");
		if (!onTake(event) || !event.getCurrentItem().isSimilar(CrateItemUtil.getCrateShard(type)) || event.getClickedInventory().getType() != InventoryType.PLAYER) {
			return false;
		}
		List<Double> info = new ArrayList<Double>();
		ItemStack item = CrateItemUtil.getCrateShard(type);
		Inventory inv = event.getWhoClicked().getInventory();
		if (inv.containsAtLeast(item, 3) && event.getInventory().getItem(49) != null && event.getInventory().getItem(51) == null) {
			info.addAll(Arrays.asList(51.0, 3.0));
		}
		else if (inv.containsAtLeast(item, 2) && event.getInventory().getItem(47) != null && event.getInventory().getItem(49) == null) {
			info.addAll(Arrays.asList(49.0, 2.0));
		}
		else if (event.getInventory().getItem(47) == null) {
			info.addAll(Arrays.asList(47.0, 1.0));
		}
		if (info.size() == 0) {
			return false;
		}
		ItemStack shard = CrateItemUtil.getCrateShard(type);
		shard.setAmount(info.get(1).intValue());
		event.getWhoClicked().getInventory().removeItem(shard);
		event.getInventory().setItem(info.get(0).intValue(), shard);
		return true;
	}
	
	private boolean canTakeShard(InventoryClickEvent event) {
		if (!onTake(event) || event.getCurrentItem().getType() != Material.PRISMARINE_SHARD || event.getClickedInventory().getType() == InventoryType.PLAYER || event.getClick() != ClickType.LEFT) {
			return false;
		}
		if (event.getSlot() == 51 && event.getCurrentItem().getAmount() == 3) {
			return true;
		}
		else if (event.getSlot() == 49 && event.getCurrentItem().getAmount() == 2 && event.getClickedInventory().getItem(51) == null) {
			return true;
		}
		else if (event.getSlot() == 47 && event.getCurrentItem().getAmount() == 1 && event.getClickedInventory().getItem(49) == null) {
			return true;
		}
		return false;
	}
	
	private boolean onPlace(InventoryClickEvent event) {
		return event.getCurrentItem() == null && event.getCursor() != null;
	}
	
	private boolean onTake(InventoryClickEvent event) {
		if (event.getCurrentItem() == null || event.getCursor() == null) {
			return false;
		}
		return event.getCurrentItem().getType() != Material.AIR && event.getCursor().getType() == Material.AIR;
	}
}
