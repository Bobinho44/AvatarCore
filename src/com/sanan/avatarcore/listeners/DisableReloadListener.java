package com.sanan.avatarcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DisableReloadListener implements Listener {

	@EventHandler
	public void onDisableReload(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("reload") || event.getMessage().equalsIgnoreCase("/reload")) event.setCancelled(true);
	}
	
}
