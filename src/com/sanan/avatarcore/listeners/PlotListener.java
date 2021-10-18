package com.sanan.avatarcore.listeners;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.nation.tribe.TribeRole;
import com.sanan.avatarcore.util.npc.NPC;
import com.sanan.avatarcore.util.npc.NPCManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.plot.Plot;
import com.sanan.avatarcore.util.plot.PlotInventoryUtil;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sanan.avatarcore.util.plot.PlotShopMenu;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import net.md_5.bungee.api.ChatColor;

public class PlotListener implements Listener {
	
	private final NPCManager npcm = NPCManager.getInstance();
	private final static PlayerManager pm = PlayerManager.getInstance();
	private final static TribeManager tm = TribeManager.getInstance();
	private final static NationManager nm = NationManager.getInstance();
	
	@EventHandler
	public void onPlotMenuInteract(InventoryClickEvent event) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException, StorageException {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (event.getClickedInventory() != null) {
			if (event.getClickedInventory().getHolder() instanceof PlotShopMenu) {
				PlotShopMenu holder = (PlotShopMenu) event.getClickedInventory().getHolder();
				event.setCancelled(true);
				if (item != null && (item.getType().equals(Material.RED_WOOL) || item.getType().equals(Material.BLUE_WOOL))) {
					String itemName = (ChatColor.stripColor((item.getItemMeta().getLore().get(0).split(" : "))[1].replaceAll(" ", "-"))).toLowerCase();
					if (itemName.matches("^" + ChatColor.stripColor(holder.getNation().getName().toLowerCase()) + "-(big|small)-plot-[0-9]*$")) {
						PlotManager plm = PlotManager.getInstance();
						Plot requestBuyPlot = plm.getPlot(itemName);
						UUID uuid = player.getUniqueId();
						
						if (plm.getPlayerPlot(player) != null) {
							Message.ALREADY_HAVE_PLOT.sendPlayer(player);
							return;
						}
						if (requestBuyPlot.getOwner() != null) {
							Message.ALREADY_SOLD_PLOT.sendPlayer(player);
							return;
						}
						if (Economy.getMoneyExact(uuid).compareTo(requestBuyPlot.getPrice()) < 0) {
							Message.NOT_ENOUGH_BUY_PLOT.sendPlayer(player);
							return;
						}
						
						requestBuyPlot.buy(player);
						Message.BUY_PLOT_SUCCESS.sendPlayer(player);
						player.openInventory(PlotInventoryUtil.getPlotShopMenu(holder.getNation(), player));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onWalkAroundPlotShopNPC(PlayerMoveEvent event) {
		for (NPC npc : npcm.getAllNPC()) {
			if (npc.getLocation().getWorld().equals(event.getTo().getWorld())) 
				if (npc.getLocation().getWorld().equals(event.getTo().getWorld())  && npc.getLocation().distance(event.getTo()) < 10) 
					npc.look(event.getPlayer(), event.getPlayer().getLocation());
			
		}
	}
	
	@EventHandler
	public void onChangeWorldToNPCWorld(PlayerChangedWorldEvent event) {
		if (event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase((String) Setting.MAIN_WORLD_NAME.get())) 
			npcm.spawnAllNPC(event.getPlayer());
	}
	
	public static void onPlotShopNPCInteract(Player player, NPC npc) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		String NPCName = npc.getName();
		for (BendingNation nation : nm.getNations()) {
			if (NPCName.contains(ChatColor.stripColor(nation.getName()))) {
				if (!nm.playerHasNation(bPlayer)) {
					player.sendMessage("You don't have a nation!");
		        	return;
				}
				if (!bPlayer.getPlayerNationData().getNationChosen().equalsIgnoreCase(nation.getName()) && nm.playerHasNation(bPlayer)) {
					Message.NO_BUY_OTHER_NATION_PLOT.sendPlayer(player);
					return;
				}
				
				BendingPlayer bendingPlayer = pm.getBendingPlayer(player);
				if (tm.playerHasTribe(bendingPlayer)) {
					BendingTribe tribe = tm.getTribe(bendingPlayer);
					TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
				    if (memberRole != TribeRole.LEADER) {
				       	player.sendMessage("Only tribe's leaders can buy a plot!");
				       	return;
				    }
				    player.openInventory(PlotInventoryUtil.getPlotShopMenu(nation, player));
				}
				else {
					player.sendMessage("You don't have a tribe!!");
				}
			}
		}
	}

}
