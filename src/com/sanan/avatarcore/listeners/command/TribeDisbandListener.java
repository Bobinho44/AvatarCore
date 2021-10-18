package com.sanan.avatarcore.listeners.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import io.papermc.paper.event.player.AsyncChatEvent;

public class TribeDisbandListener implements Listener {

	private static List<Player> disbanding = new ArrayList<>();
	
	@EventHandler
	public void onTribeDisband(AsyncPlayerChatEvent event) throws NoLoanPermittedException, ArithmeticException, StorageException, UserDoesNotExistException {
		String msg = event.getMessage();
		Player player = event.getPlayer();
		
		if (disbanding.contains(player) && msg.equalsIgnoreCase("confirm")) {
			event.setCancelled(true);
			//Disband tribe
			BendingPlayer bPlayer = PlayerManager.getInstance().getBendingPlayer(player);
			BendingTribe tribe = TribeManager.getInstance().getTribe(bPlayer);
			tribe.sendTribeMessage(Message.color(Message.TRIBE_DISBAND_GLOBAL.getMessage()));
			tribe.disband();
			TribeManager.getInstance().unregisterTribe(tribe);
			removeDisbanding(player);
		}
	}
	
	public static void addDisbanding(Player player) {
		disbanding.add(player);
	}
	
	public static void removeDisbanding(Player player) {
		if (disbanding.contains(player)) disbanding.remove(player);
	}
	
	
	
}
