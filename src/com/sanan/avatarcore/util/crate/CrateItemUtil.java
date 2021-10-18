package com.sanan.avatarcore.util.crate;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.item.ItemBuilder;

import org.bukkit.ChatColor;


public class CrateItemUtil {

	public static ItemStack getCrateMenuDivider(Material material) {
		return new ItemBuilder(material)
				.setName("")
				.addLore("")
				.build();
	}
	
	public static ItemStack getSpinner() {
		return new ItemBuilder(Material.SLIME_BALL)
				.setName(ChatColor.AQUA + "Click to spin!")
				.build();
	}
	
	public static ItemStack getGlowingDivider(int step) {
		ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		if (step % 2 == 0) {
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
			item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		} else {
			item.removeEnchantment(Enchantment.KNOCKBACK);
			item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return new ItemBuilder(item)
				.setName("")
				.addLore("")
				.build();
	}
	
	public static ItemStack getInformationBook() {
		return new ItemBuilder(Material.BOOK)
				.setName(ChatColor.AQUA + "How to use")
				.addLore(ChatColor.GRAY +"• Slot 1 takes 1 shard", 
						 ChatColor.GRAY +"• Slot 2 takes 2 shards and slot 1 must be filled", 
						 ChatColor.GRAY + "• Slot 3 takes 3 shards and slot 2 must be filled")
				.build();
	}
	
	public static ItemStack getCrateShard(String type) {
		ItemBuilder item = new ItemBuilder(Material.PRISMARINE_SHARD);
		switch (type.toLowerCase()) {
		case "whitelotus":
			item.setName(ChatColor.WHITE + "[" + ChatColor.WHITE + ChatColor.BOLD + "WhiteLotus" + ChatColor.WHITE + "]")
			    .setLore(ChatColor.DARK_RED + "This shard has the ability to conger the most powerful items in existence!");
			break;
		case "legend":
			item.setName(ChatColor.GOLD + "Legend")
			    .setLore(ChatColor.DARK_PURPLE + "This shard has the ability to conger legendary items!");
			break;
		case "lord":
			item.setName(ChatColor.DARK_PURPLE + "Lord")
				.setLore(ChatColor.GOLD + "This shard has the ability to conger powerful items!");
			break;
		case "master":
			item.setName(ChatColor.DARK_RED + "Master")
				.setLore(ChatColor.GOLD + "This shard has the ability to conger powerful items!");
			break;
		case "champion":
			item.setName(ChatColor.AQUA + "Champion")
				.setLore(ChatColor.GOLD + "This shard has the ability to conger powerful items!");
			break;
		default:
			item.setName(ChatColor.LIGHT_PURPLE + "Hero")
				.setLore(ChatColor.GOLD + "This shard has the ability to conger powerful items!");
			break;
		}
		item.addLore("§7Right click to open menu", "§7Once menu is open left click to place in a slot");
		return item.build();
				
	}
	
}
