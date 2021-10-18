package com.sanan.avatarcore.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.crate.BendingCrateManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.ess3.api.InvalidWorldException;

public class TutorialListener implements Listener {

	private final static Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	private final BendingCrateManager bcm = BendingCrateManager.getInstance();
	private final static PlayerManager pm = PlayerManager.getInstance();
	
	@EventHandler
	public void onPickUpShard(PlayerPickupItemEvent event) {
		BendingPlayer bPlayer = pm.getBendingPlayer((Player) event.getPlayer());
		if (event.getItem().getItemStack().getType() == Material.PRISMARINE_SHARD && bPlayer != null && !bPlayer.isInBendingMode()) {
			String type = ChatColor.stripColor(event.getItem().getItemStack().getItemMeta().getDisplayName().replace("[", "").replace("]", ""));
			if (bcm.getItemsCollection(type).size() == 0) return;
			if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() == 6.0) {
				bPlayer.nextStepTutorial();
			}
		}
	}
	
	@EventHandler
	public void onDropDuringTutorial(PlayerDropItemEvent event) {
		if (event.getPlayer().getWorld().getName().equalsIgnoreCase("tutorial")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onJoinDuringTutorial(PlayerJoinEvent event) {
		Player player = (Player) event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bPlayer.hasFinishTutorial()) {
			double step = bPlayer.getTutorialStep();
			if (step >= 2 && step  <= 6) {
				bPlayer.toggleBendingMode();
			}
			if (step >= 5 && step <= 6) {
				for (BendingHotbar playerHotbars : bPlayer.getHotbars()) {
					bPlayer.setEquippedHotbar(playerHotbars);
					bPlayer.applyBendingInventory();
					bPlayer.applyBendingHotbarAbilities();
				}
			}
		}
	}
	
	@EventHandler
	public void onQuitDuringTutorial(PlayerQuitEvent event) {
		Player player = (Player) event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bPlayer.hasFinishTutorial()) {
			bPlayer.getTutorial().restorePlayerInfo();
			for (Entity entity : new ArrayList<Entity>(Bukkit.getWorld("tutorial").getEntities())) {
				if ((entity.getType() == EntityType.VILLAGER || entity.getType() == EntityType.DROPPED_ITEM) && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(player.getName())) {
					entity.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onFinishTutorial(PlayerMoveEvent event) throws WarpNotFoundException, InvalidWorldException {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		double x = event.getTo().getBlock().getLocation().getX();
		double y = event.getTo().getBlock().getLocation().getY(); 
		double z = event.getTo().getBlock().getLocation().getZ();
		if (event.getTo().getWorld().getName().equalsIgnoreCase("tutorial") && !bPlayer.hasFinishTutorial() && (bPlayer.getTutorialStep() == 8 || bPlayer.isSkipped())) {
			if (x == -51 && y >= 5 && y <= 7 && z >= -273 && z <= -272) {
				bPlayer.setStep(8.0);
				player.teleport(essentials.getWarps().getWarp("spawn"));
				bPlayer.nextStepTutorial();
			}
		}
	}
	
	@EventHandler
	public void onChatDuringTutorial(AsyncChatEvent event) {
		if (event.getPlayer().getWorld().getName().equalsIgnoreCase("tutorial")) {
			event.setCancelled(true);
			return;
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equalsIgnoreCase("tutorial")) {
				event.viewers().remove(player);
			}
		}
	}
	
	@EventHandler
	public void onUseCommandDuringTutorial(PlayerCommandPreprocessEvent event) {
		if (!pm.getBendingPlayer(event.getPlayer()).hasFinishTutorial() && !event.getMessage().contains("skip")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTeleportDuringTutorial(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (event.getFrom().getWorld().getName().equalsIgnoreCase("tutorial") && bPlayer.getTutorialStep() > 0 && bPlayer.getTutorialStep() < 8 && !bPlayer.isSkipped()) {
			event.setCancelled(true);
		}
	}
	
	public static void onInteractWithTrainerDuringTutorial(Player player) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bPlayer.getTutorialStep() == Math.round(bPlayer.getTutorialStep())) {
			bPlayer.previoustepTutorial();
		}
	}
	
	public static void onInteractWithTrainerToStartTutorial(Player player) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		try {
			player.teleport(essentials.getWarps().getWarp("tutorial"));
			bPlayer.startTutorial(1);
		} catch (WarpNotFoundException | InvalidWorldException e) {}
	}
	
}
