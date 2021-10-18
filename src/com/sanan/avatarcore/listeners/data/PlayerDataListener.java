package com.sanan.avatarcore.listeners.data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.npc.NPCManager;
import com.sanan.avatarcore.util.player.PlayerManager;

public class PlayerDataListener implements Listener {
	
	private static PlayerManager pm = PlayerManager.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (player.getLocation().getWorld().getName().equalsIgnoreCase((String) Setting.MAIN_WORLD_NAME.get())) 
			NPCManager.getInstance().spawnAllNPC(player);
		
		if (pm.playerDataExists(player)) {
			pm.loadPlayerData(player);
		}
		else {
			pm.createRegisterBendingPlayer(player);
			pm.savePlayerData(pm.getBendingPlayer(player));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		pm.savePlayerData(pm.getBendingPlayer(player));
		pm.unregisterPlayer(pm.getBendingPlayer(player));
	}
	
	
}
