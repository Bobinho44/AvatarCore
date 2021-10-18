package com.sanan.avatarcore.util.bending.ability.hotbar;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.player.BendingPlayer;

import net.md_5.bungee.api.ChatColor;

public class BendingItemUtil {
	
	private static BendingAbilityManager bam = BendingAbilityManager.getInstance();
	
	//Hotbar Display Item
	public static ItemStack getHotbarDisplayItem(BendingHotbar hotbar) {
		ItemBuilder builder = new ItemBuilder(Material.GLOBE_BANNER_PATTERN)
				.setName(ChatColor.GOLD + hotbar.getName())
				.addLore(ChatColor.GRAY + "-----==== " + ChatColor.AQUA + "Abilities" + ChatColor.GRAY + " ====-----");
		for (int i=0; i<9; i++) {
			BendingAbility ability = hotbar.getAbilities()[i];
			String disp = ability == null ? "None" : ability.getName();
			builder.addLore(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i+1) + ". " + disp);
		}
		builder.addLore(ChatColor.GRAY + "-----==== " + ChatColor.AQUA + "Abilities" + ChatColor.GRAY + " ====-----");
		return builder.build();
	}
	
	
	/*
	 * Manage Bending Hotbars
	 */
	
	//Access menu item
	public static ItemStack getManageHotbarsAccessor() {
		return new ItemBuilder(Material.SLIME_BALL)
				.setName(ChatColor.AQUA + "Manage Bending Hotbars")
				.addLore(ChatColor.GRAY + "Manage and customize your bending hotbars with abilities of your choice.", ChatColor.GOLD + "Click to open")
				.build();
	}
	
	//Divider Item
	public static ItemStack getManageHotbarsDivider() {
		return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
				.setName("")
				.addLore("")
				.build();
	}
	
	//New hotbar item
	public static ItemStack getManageHotbarsCreate() {
		return new ItemBuilder(Material.ENDER_EYE)
				.setName(ChatColor.YELLOW + "Create new hotbar")
				.addLore(ChatColor.GRAY + " - Click on an existing hotbar to manage its abilities, rename, or remove it",
						ChatColor.GRAY + " - Hotbars can be equipped from your player inventory while in bending mode",
						ChatColor.GRAY + " - " + ChatColor.GOLD + "Click to create new hotbar using unlocked abilities")
				.build();
	}

	//Active passive ability
	public static ItemStack getManageHotbarsPassiveAbility() {
		return new ItemBuilder(Material.HEART_OF_THE_SEA)
				.setName(ChatColor.YELLOW + "Choose your passive abilitites")
				.addLore(ChatColor.GRAY + " - Click on a passive ability to select or deselect it",
						ChatColor.GRAY + " - You can select 4 ability + 1 for each selected class")
				.build();
	}
	
	/*
	 * Abilities View
	 */
	public static ItemStack getChooseElementInfo() {
		return new ItemBuilder(Material.NAME_TAG)
				.setName(ChatColor.YELLOW + "Choose an Element")
				.addLore(ChatColor.GRAY + "- Click on an element to view your abilities for that element")
				.build();
	}
	public static ItemStack getHotbarAbilitiesInfo() {
		return new ItemBuilder(Material.END_CRYSTAL)
				.setName(ChatColor.YELLOW + "Editing Hotbar Abilities")
				.addLore(ChatColor.GRAY + "- " + ChatColor.BOLD + "Left Click" + ChatColor.GRAY + " on a slot to choose an ability to assign to it",
						ChatColor.GRAY + "- " + ChatColor.BOLD + "Right Click" + ChatColor.GRAY + " on a slot to clear its existing ability")
				.build();
	}
	
	public static ItemStack getHotbarAbilitiesEmptyAbility() {
		return new ItemBuilder(Material.BARRIER)
				.setName(ChatColor.LIGHT_PURPLE + "EMPTY")
				.addLore(ChatColor.GRAY + "- No ability has been assigned to this slot")
				.build();
	}
	
	//Fire Element Item
	public static ItemStack getFireElementItem(BendingPlayer... bPlayer) {
		boolean player = bPlayer.length > 0 && bPlayer[0] != null;
		ArrayList<String> lore = new ArrayList<String>();
		if (player) {
			lore.addAll(Arrays.asList(" ", ChatColor.GOLD + "Level: " + ChatColor.YELLOW + ChatColor.BOLD + 
					bPlayer[0].getLevel("fire"), " ", ChatColor.GOLD + "Unlocked Abilities:"));
			lore.addAll(bam.getPlayerUnlockedAbility(bPlayer[0], "fire", false));
		}
		return new ItemBuilder(Material.BLAZE_POWDER)
				.setName(ChatColor.RED.toString() + ChatColor.BOLD + "Fire")
				.addLore(lore.toArray(new String[0]))
				.build();
	}
	//Earth Element Item
	public static ItemStack getEarthElementItem(BendingPlayer... bPlayer) {
		boolean player = bPlayer.length > 0 && bPlayer[0] != null;
		ArrayList<String> lore = new ArrayList<String>();
		if (player) {
			lore.addAll(Arrays.asList(" ", ChatColor.GOLD + "Level: " + ChatColor.YELLOW + ChatColor.BOLD + 
					bPlayer[0].getLevel("earth"), " ", ChatColor.GOLD + "Unlocked Abilities:"));
			lore.addAll(bam.getPlayerUnlockedAbility(bPlayer[0], "earth", false));
		}
		return new ItemBuilder(Material.OAK_SAPLING)
				.setName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Earth")
				.addLore(lore.toArray(new String[0]))
				.build();
	}
	//Air Element Item
	public static ItemStack getAirElementItem(BendingPlayer... bPlayer) {
		boolean player = bPlayer.length > 0 && bPlayer[0] != null;
		ArrayList<String> lore = new ArrayList<String>();
		if (player) {
			lore.addAll(Arrays.asList(" ", ChatColor.GOLD + "Level: " + ChatColor.YELLOW + ChatColor.BOLD + 
					bPlayer[0].getLevel("air"), " ", ChatColor.GOLD + "Unlocked Abilities:"));
			lore.addAll(bam.getPlayerUnlockedAbility(bPlayer[0], "air", false));
		}
		return new ItemBuilder(Material.FEATHER)
				.setName(ChatColor.GRAY.toString() + ChatColor.BOLD + "Air")
				.addLore(lore.toArray(new String[0]))
				.build();
	}
	//Water Element Item
	public static ItemStack getWaterElementItem(BendingPlayer... bPlayer) {
		boolean player = bPlayer.length > 0 && bPlayer[0] != null;
		ArrayList<String> lore = new ArrayList<String>();
		if (player) {
			lore.addAll(Arrays.asList(" ", ChatColor.GOLD + "Level: " + ChatColor.YELLOW + ChatColor.BOLD + 
					bPlayer[0].getLevel("water"), " ", ChatColor.GOLD + "Unlocked Abilities:"));
			lore.addAll(bam.getPlayerUnlockedAbility(bPlayer[0], "water", false));
		}
		return new ItemBuilder(Material.WATER_BUCKET)
				.setName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Water")
				.addLore(lore.toArray(new String[0]))
				.build();
	}
	
	

	/*
	 * Selected Hotbar
	 */
	
	//Rename Selected Hotbar Item
	public static ItemStack getSelectedHotbarRename() {
		return new ItemBuilder(Material.NAME_TAG)
				.setName(ChatColor.YELLOW + "Rename Hotbar")
				.addLore(ChatColor.GRAY + "Rename the selected hotbar",
						ChatColor.GOLD + "Click to rename hotbar")
				.build();
	}
	
	//Edit Selected Hotbar Item
	public static ItemStack getSelectedHotbarEdit() {
		return new ItemBuilder(Material.ANVIL)
				.setName(ChatColor.YELLOW + "Edit Hotbar")
				.addLore(ChatColor.GRAY + "Edit the selected hotbar's bounded abilities",
						ChatColor.GOLD + "Click to edit hotbar")
				.build();
	}
	
	//Remove Selected Hotbar Item
	public static ItemStack getSelectedHotbarRemove() {
		return new ItemBuilder(Material.BARRIER)
				.setName(ChatColor.YELLOW + "Remove Hotbar")
				.addLore(ChatColor.GRAY + "Remove the selected hotbar from your presets",
						ChatColor.GOLD + "Click to remove hotbar",
						ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.RED + "This action cannot be undone")
				.build();
	}
	
}
