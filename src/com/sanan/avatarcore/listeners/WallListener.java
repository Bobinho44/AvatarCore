package com.sanan.avatarcore.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingFallingBlock;
import com.sanan.avatarcore.util.bendingwall.BendingWall;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public class WallListener  implements Listener {

	private final WallManager wm = WallManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	
	@EventHandler
	public void onWallExplode(EntityExplodeEvent event) {
		for (Block block : new ArrayList<Block>(event.blockList())) {
			BendingWall wall = wm.isFromWall(block.getLocation());
			if (wall != null) {
				for (Block explodedBlock : new ArrayList<Block>(event.blockList())) {
					if (!explodedBlock.getType().equals(Material.TNT)) {
						event.blockList().remove(explodedBlock);
					}
				}
				wall.damageWall(4);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBendingAbilityUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			BendingWall wall = wm.isFromWall(event.getClickedBlock().getLocation());
			if (wall != null) {
				event.setCancelled(true);
				if (!bPlayer.isInBendingMode() && wall.getCooldown() != 0) {
					float damage = 0;
					if (event.getItem() != null) {
						String[] itemName = event.getItem().getType().name().split("_");
						
						if (itemName[0].equalsIgnoreCase("netherite")) {
							damage += 2;
							if (itemName[1].equalsIgnoreCase("axe") || itemName[1].equalsIgnoreCase("sword"))
								damage += 0.5;
						}
						
						else if (itemName[0].equalsIgnoreCase("diamond")) {
							damage += 1.5;
							if (itemName[1].equalsIgnoreCase("axe") || itemName[1].equalsIgnoreCase("sword"))
								damage += 0.5;
						}
						
						else if (itemName[0].equalsIgnoreCase("iron")) {
							damage += 1;
							if (itemName[1].equalsIgnoreCase("axe") || itemName[1].equalsIgnoreCase("sword"))
								damage += 0.5;
						}
						
						else if (itemName[0].equalsIgnoreCase("stone")) {
							damage += 0.5;
							if (itemName[1].equalsIgnoreCase("axe") || itemName[1].equalsIgnoreCase("sword"))
								damage += 0.5;
						}
						
						else if (itemName[0].equalsIgnoreCase("golden")) {
							if (itemName[1].equalsIgnoreCase("axe") || itemName[1].equalsIgnoreCase("sword"))
								damage += 0.5;
						}
						
						damage += event.getItem().getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 1;
					}
					wall.damageWall(damage);
					
					if (damage == 0)
						player.sendMessage("Your attacks with this weapon do not seem to be damaging the wall...");
				}
			}
		}
	}
	
	@EventHandler
	public void onBreakBlockBehingWall(BlockBreakEvent event) {
		Location location = event.getBlock().getLocation();
		while (BendingFallingBlock.isEarthBendingFallingBlock(location.add(0, 1, 0).getBlock().getType())) {
			if (wm.isFromWall(location) != null) {
				event.getPlayer().sendMessage("You can't attack the foundation of this wall!");
				event.setCancelled(true);
				return;
			}
		}
	}
}
