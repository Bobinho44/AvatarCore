package com.sanan.avatarcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import com.sanan.avatarcore.util.npc.NPC;
import com.sanan.avatarcore.util.npc.NPCManager;

import net.md_5.bungee.api.ChatColor;

public class NPCListener implements Listener {

	private final static NPCManager npcm = NPCManager.getInstance();
	
	public static void onInteractWithNPC(PacketEvent event) {
		Player player = event.getPlayer();
		PacketContainer packet = event.getPacket();		
		if (npcm.isNPC(packet.getIntegers().read(0))) {
			NPC npc = npcm.getNPC(packet.getIntegers().read(0));
			if (npc.getLocation().getWorld().getName().equalsIgnoreCase("avatarmap")) {
				if (ChatColor.stripColor(npc.getName()).equalsIgnoreCase("[trainer]")) {
					TutorialListener.onInteractWithTrainerToStartTutorial(player);
				}
				else {
					PlotListener.onPlotShopNPCInteract(player, npc);
				}
			}
			else if (npc.getLocation().getWorld().getName().equalsIgnoreCase("tutorial")) {
				TutorialListener.onInteractWithTrainerDuringTutorial(player);
			}
		}
	}
	
}
