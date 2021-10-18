package com.sanan.avatarcore.util.plot;

import java.math.BigDecimal;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.util.item.ItemBuilder;

import net.md_5.bungee.api.ChatColor;

public class PlotItemUtil {

	public static ItemStack getPlotMenuDivider() {
		return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
				.setName("")
				.addLore("")
				.build();
	}
	
	public static ItemStack getPlotMenuPlayerInfo(Player player) {
		try {
			return new ItemBuilder(Material.IRON_DOOR)
					.setName(ChatColor.YELLOW + "Informations: ")
					.addLore(ChatColor.GOLD + "Money: " + ChatColor.YELLOW + "$" + Economy.getMoneyExact(player.getUniqueId()))
					.addLore(ChatColor.GOLD + "Has a plot: " + ChatColor.YELLOW + (PlotManager.getInstance().getPlayerPlot(player) == null ? "No" : "Yes"))
					.build();
		} catch (UserDoesNotExistException e) {}
		return null;
	}
	
	public static ItemStack getPlotItem(Plot plot) {
		OfflinePlayer owner = plot.getOwner();
		BigDecimal price = plot.getPrice();
		Material material = price.compareTo(BigDecimal.valueOf(500000)) > 0 ? Material.BLUE_WOOL : Material.RED_WOOL;
		String location = (plot.getCoordinates()[0].add(plot.getCoordinates()[1])).divide(2).toString().replaceAll("[()]", "");
		
		ItemBuilder builder = new ItemBuilder(material)
				.setName(ChatColor.YELLOW + (owner == null ? "For sale" : "Sold"))
				.addLore(ChatColor.GOLD + "Name : " + ChatColor.RED + plot.getName());
				if (owner != null) {
					builder.addLore(ChatColor.GOLD + "Owner : " + ChatColor.RED + owner.getName())
					.build().setType(Material.BLACK_STAINED_GLASS_PANE);
				}
				else {
					builder.addLore(ChatColor.GOLD + "Price : " + ChatColor.RED + "$" + price);
				}
				builder.addLore(ChatColor.GOLD + "Size : " + ChatColor.RED + (price.compareTo(BigDecimal.valueOf(500000)) > 0 ? "30x30" : "20x20"))
				.addLore(ChatColor.GOLD + "Location : " + ChatColor.RED + location);
			return builder.build();
		}
	
}
