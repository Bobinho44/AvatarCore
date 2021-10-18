package com.sanan.avatarcore.util.bending.ability.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.HotbarChooseElementHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.EditBendingHotbarHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.HotbarAbilitySelectHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.ManageBendingHotbarsHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.PassiveAbilitySelectHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.SelectedBendingHotbarHolder;
import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.player.BendingPlayer;

import net.md_5.bungee.api.ChatColor;

public class BendingInventoryUtil {
	
	//Hotbar Ability Select Inventory
	public static Inventory getHotbarAbilitySelectInventory(BendingHotbar hotbar, int slot, BendingPlayer bPlayer, BendingElement element) {
		Inventory inv = Bukkit.createInventory(new HotbarAbilitySelectHolder(hotbar, slot), 54, "§6Choose an ability for slot §e" + slot);
		String bendingClass = ChatColor.stripColor(element.getName().toLowerCase());
		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();
		
		for (int i=45; i<54; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(49, new ItemBuilder(element.getItem()).setLore(" ", ChatColor.GOLD + "Level: " + ChatColor.YELLOW + ChatColor.BOLD + 
				bPlayer.getLevel(bendingClass)).build());
	
		for (BendingAbility ability : BendingAbilityManager.getInstance().getAllLoadedAbilities(false)) {
			if (ability.getElement().equals(element) && !(ability.getName().equalsIgnoreCase("earth levitation") && bPlayer.getLevel("earth") >= 55)) {
				ItemStack item = ability.getItem();
				if (ability.getLevelRequired() > bPlayer.getLevel(bendingClass) || (!bPlayer.hasFinishTutorial() && ability.getLevelRequired() != 1)) {
					item.setType(Material.BLACK_STAINED_GLASS_PANE);
				}
				inv.addItem(item);
			}
		}
		
		return inv;
	}
	
	//Passive Ability Select Inventory
	public static Inventory getPassiveAbilitySelectInventory(BendingPlayer bPlayer) {
		Inventory inv = Bukkit.createInventory(new PassiveAbilitySelectHolder(), 54, "§6Activate your passive abilities");
		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();
		
		for (int i=45; i<54; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(49, new ItemBuilder(Material.HEART_OF_THE_SEA)
					.setName(ChatColor.GOLD + "Bending Level:")
					.addLore(" ", ChatColor.GOLD + "Air: Level: " + ChatColor.YELLOW + ChatColor.BOLD +	bPlayer.getLevel("air"))
					.addLore(" ", ChatColor.GOLD + "Earth: Level: " + ChatColor.YELLOW + ChatColor.BOLD +	bPlayer.getLevel("earth"))
					.addLore(" ", ChatColor.GOLD + "Fire: Level: " + ChatColor.YELLOW + ChatColor.BOLD +	bPlayer.getLevel("fire"))
					.addLore(" ", ChatColor.GOLD + "Water: Level: " + ChatColor.YELLOW + ChatColor.BOLD +	bPlayer.getLevel("water"))
					.build());
	
		for (BendingAbility ability : BendingAbilityManager.getInstance().getAllLoadedAbilities(true)) {
			ItemStack item = ability.getItem();
			if (ability.getLevelRequired() > bPlayer.getLevel(ChatColor.stripColor(ability.getElement().getName().toLowerCase()))) {
				item.setType(Material.BLACK_STAINED_GLASS_PANE);
			}
			if (bPlayer.getPassiveAbilityActivated().contains(ability)) {
				item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
				item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			inv.addItem(item);
		}
		
		return inv;
	}
	
	//Hotbar Ability Choose Element Inventory
	public static Inventory getHotbarChooseElementInventory(BendingHotbar hotbar, int slot, BendingPlayer bPlayer) {
		Inventory inv = Bukkit.createInventory(new HotbarChooseElementHolder(hotbar, slot), 27, "§6Assigning ability to slot: §e" + slot);

		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();

		for (int i=0; i<27; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(4, BendingItemUtil.getChooseElementInfo());
		inv.setItem(10, BendingItemUtil.getFireElementItem(bPlayer));
		inv.setItem(12, BendingItemUtil.getEarthElementItem(bPlayer));
		inv.setItem(14, BendingItemUtil.getAirElementItem(bPlayer));
		inv.setItem(16, BendingItemUtil.getWaterElementItem(bPlayer));
	
		return inv;
	}
	
	
	//Hotbar Ability View Inventory
	public static Inventory getEditBendingHotbarInventory(BendingHotbar hotbar) {
		Inventory inv = Bukkit.createInventory(new EditBendingHotbarHolder(hotbar), 27, "§eEditing Hotbar: §6" + hotbar.getName());

		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();
		
		for (int i=0; i<27; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(4, BendingItemUtil.getHotbarAbilitiesInfo());
		
		BendingAbility[] abilities = hotbar.getAbilities();
		int j=0;
		for (int i=9; i<18; i++) {
			if (abilities[j] == null) 
				inv.setItem(i, BendingItemUtil.getHotbarAbilitiesEmptyAbility());
			else 
				inv.setItem(i, abilities[j].getItem());
			j++;
		}
		
		return inv;
	}
	
	
	//Selected Hotbar Inventory
	public static Inventory getSelectedBendingHotbarInventory(BendingHotbar hotbar) {
		Inventory inv = Bukkit.createInventory(new SelectedBendingHotbarHolder(hotbar), 27, "§bChoose Hotbar Action");

		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();
		ItemStack rename = BendingItemUtil.getSelectedHotbarRename();
		ItemStack edit = BendingItemUtil.getSelectedHotbarEdit();
		ItemStack remove = BendingItemUtil.getSelectedHotbarRemove();
		
		for (int i=0; i<27; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(4, BendingItemUtil.getHotbarDisplayItem(hotbar));
		inv.setItem(11, rename);
		inv.setItem(13, edit);
		inv.setItem(15, remove);
		
		return inv;
	}
	
	//Managing Hotbars Inventory
	public static Inventory getManageBendingHotbarInventory(BendingPlayer player) {
		Inventory inv = Bukkit.createInventory(new ManageBendingHotbarsHolder(), 18, "§bManage Bending Hotbars");
		
		ItemStack divider = BendingItemUtil.getManageHotbarsDivider();
		ItemStack create = BendingItemUtil.getManageHotbarsCreate();
		
		for (int i=9; i<18; i++) {
			inv.setItem(i, divider);
		}
		inv.setItem(13, create);
		
		for (BendingHotbar hotbar : player.getHotbars()) {
			inv.addItem(BendingItemUtil.getHotbarDisplayItem(hotbar));
		}
		
		return inv;
	}
	
}
