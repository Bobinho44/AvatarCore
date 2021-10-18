package com.sanan.avatarcore.util.nation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class BendingNation {

	private final TribeManager tm = TribeManager.getInstance();
	
	private final String name;
	private final ChatColor color;
	private final ItemStack item;
	private final BendingElement element;

	private List<UUID> players;
	private int maxCapacity;
	private String leaderUUID;

	public BendingNation(final String name, final ChatColor color, int maxCapacity, String leaderUUID, final ItemStack item) {
		this.name = name;
		this.color = color;
		this.item = item;
		this.players = new ArrayList<>();
	    this.maxCapacity = maxCapacity;
		this.leaderUUID = leaderUUID;
		this.element = BendingElement.getBendingElement(name.toLowerCase());
	}

	public boolean isLeaderOnline() {
		if (Bukkit.getPlayer(UUID.fromString(this.leaderUUID)) == null) return false;
		else if (Bukkit.getPlayer(UUID.fromString(this.leaderUUID)).isOnline()) return true;
		else return false;
	}

	public String getLeaderUUID() {
		return leaderUUID;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}
	
	/**
	 * Return a UUID List of members in this BendingNation
	 * @return List<UUID>
	 */
	public List<UUID> getPlayers() {
		return players;
	}
	
	/**
	 * Return a List<String> of Player Names that are online in this BendingNation
	 * @return List<String>
	 */
	public List<String> getOnlinePlayers() {
		List<String> online = new ArrayList<>();
		for (UUID uuid : this.players) {
			if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) online.add(Bukkit.getPlayer(uuid).getName());
		}
		return online;
	}
	
	/**
	 * Return a List<String> of Player Names that are offline in this BendingNation
	 * @return List<String>
	 */
	public List<String> getOfflinePlayers() {
		List<String> offline = new ArrayList<>();
		for (UUID uuid : this.players) {
			if (Bukkit.getPlayer(uuid) == null) {
				//TODO: Mojang has a rate limit on the amount of times you can lookup a UUID on their API, We may need to look into caching
				offline.add(Bukkit.getOfflinePlayer(uuid).getName());
			}
			else if (!Bukkit.getPlayer(uuid).isOnline()) offline.add(Bukkit.getPlayer(uuid).getName());
		}
		return offline;
	}
	
	/**
	 * Add a BendingPlayer to this BendingNation
	 * @param BendingPlayer
	 */
	public void addPlayer(BendingPlayer player) {
		BendingTribe tribe = tm.getTribe(player);
		if (tribe != null) tribe.theLeaderIsRogue(false);
		addPlayer(player.getSpigotPlayer());
	}
	
	public void addPlayer(Player player) {
		addPlayer(player.getUniqueId());
	}
	
	public void addPlayer(UUID player) {
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + Bukkit.getOfflinePlayer(player).getName() + " parent add " + ChatColor.stripColor(getName()) + "-nation");
		this.players.add(player);
	}
	
	public void kick(BendingPlayer player) {
		removePlayer(player);
	}
	
	
	public void removePlayer(BendingPlayer player) {
		BendingTribe tribe = tm.getTribe(player);
		if (tribe != null) {
			tribe.theLeaderIsRogue(true);
		}
		removePlayer(player.getSpigotPlayer());
        AvatarCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AvatarCore.getInstance(), new Runnable() {
			public void run() {
				try {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getSpigotPlayer().getName() + " parent remove " + ChatColor.stripColor(getName()) + "-nation");
				} catch (Exception e){}
			}
		});
	}
	
	public void removePlayer(Player player) {
		removePlayer(player.getUniqueId());
	}
	
	public void removePlayer(UUID player) {
		if (this.players.contains(player)) this.players.remove(player);
	}
	
	/**
	 * Check if BendingNation contains BendingPlayer
	 * @param player
	 * @return
	 */
	public boolean hasPlayer(BendingPlayer player) {
		return hasPlayer(player.getSpigotPlayer());
	}
	
	private boolean hasPlayer(Player player) {
		return hasPlayer(player.getUniqueId());
	}
	
	private boolean hasPlayer(UUID player) {
		return this.players.contains(player) || this.leaderUUID.equalsIgnoreCase(player.toString());
	}
	
	public int getPlayerCount() {
		return this.players.size();
	}

	public String getName() {
		return this.getColor() + this.name;
	}

	/**
	 * Get the Color associated with this Nation
	 * @return ChatColor
	 */
	public ChatColor getColor() {
		return this.color;
	}

	public BendingElement getBendingElement() {
		return this.element;
	}

	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
