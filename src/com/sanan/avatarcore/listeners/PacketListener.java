package com.sanan.avatarcore.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sanan.avatarcore.AvatarCore;

public class PacketListener {
	
	private static List<Player> debugMultipleCallEvent = new ArrayList<>();
	private final static AvatarCore ac = AvatarCore.getInstance();
	
	public static void createUseEntityPacketListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ac, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			public void onPacketReceiving(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
					if (!debugMultipleCallEvent.contains(event.getPlayer())) {
						debugMultipleCallEvent.add(event.getPlayer());
						
						new BukkitRunnable() {
					        public void run() {
					        	debugMultipleCallEvent.remove(event.getPlayer());
					        }
					    }.runTaskLaterAsynchronously(ac, 1);
						ac.getServer().getScheduler().scheduleSyncDelayedTask(AvatarCore.getInstance(), new Runnable() {
							public void run() {
								try {
									NPCListener.onInteractWithNPC(event);;
								} catch (Exception e){}
							}
						});
					}
				}
			}
		});
	}
	
}
