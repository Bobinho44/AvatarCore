package com.sanan.avatarcore.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public class LevelListener implements Listener {

	private final PlayerManager pm = PlayerManager.getInstance();
	
	@EventHandler
	public void onMineOre(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		List<Material> otherOres = new ArrayList<Material>(Arrays.asList(Material.COAL_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE, Material.ANCIENT_DEBRIS));
		Player player = (Player) event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		int xp = 0;
		if (event.getBlock().getType().equals(Material.EMERALD_ORE))
			xp += 120;
		else if (event.getBlock().getType().equals(Material.DIAMOND_ORE))
			xp += 100;
		else if (event.getBlock().getType().equals(Material.IRON_ORE))
			xp += 90;
		else if (otherOres.contains(event.getBlock().getType()))
			xp += 50;
		else
			xp += 1;
		if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
			xp = 2;
		}
		bPlayer.addXp(xp);
	}
	
	@EventHandler
	public void onUseRedstoneWithdraw(PlayerInteractEvent event) {
		if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND) || event.getItem() == null) return;
		ItemStack item = event.getItem();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item.getType() == Material.NETHER_STAR && item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				if (ChatColor.stripColor(lore.get(0)).equalsIgnoreCase("Bending XP")) {
					pm.getBendingPlayer(event.getPlayer()).addXp(Integer.valueOf(ChatColor.stripColor(lore.get(2)).replace("[", "").replace("]", "")));
					event.getPlayer().getInventory().setItemInMainHand(null);
				}
			}
		}
	}
}
