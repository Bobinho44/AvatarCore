package com.sanan.avatarcore.util.nation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.sanan.avatarcore.util.bending.ability.hotbar.BendingItemUtil;
import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import org.bukkit.entity.Player;


public class NationManager {

	/*
	 * CONSTANTS
	 */
	private static final UUID FIRE_LEADER = UUID.fromString("a59a7691-c074-49c6-a023-13676e52a98b");
	private static final UUID WATER_LEADER = UUID.fromString("a59a7691-c074-49c6-a023-13676e52a98b");
	private static final UUID EARTH_LEADER = UUID.fromString("a59a7691-c074-49c6-a023-13676e52a98b");
	private static final UUID AIR_LEADER = UUID.fromString("a59a7691-c074-49c6-a023-13676e52a98b");
	
	private static NationManager instance = null;

	private static List<BendingNation> nations;
	private final Set<UUID> playersAboutToLeave;
	
	public static NationManager getInstance() {
		if (instance == null) instance = new NationManager();
		return instance;
	}
	
	private NationManager() {
		nations = new ArrayList<>();
		this.playersAboutToLeave = ConcurrentHashMap.newKeySet();
	}

	public void addLeaveRequest(Player player) {
		this.playersAboutToLeave.add(player.getUniqueId());
	}

	public boolean isPlayerAboutToLeave(Player player) {
		return this.playersAboutToLeave.contains(player.getUniqueId());
	}

	public boolean removeLeaveRequest(Player player) {
		return this.playersAboutToLeave.remove(player.getUniqueId());
	}
	
	public List<BendingPlayer> getRogues() {
		List<BendingPlayer> rogues = new ArrayList<>();
		for (BendingPlayer player : PlayerManager.getInstance().getPlayers()) {
			if (!playerHasNation(player)) rogues.add(player);
		}
		return rogues;
	}
	
	//Constant Accessors
	public BendingNation getFireNation() {
		return getNation("Fire");
	}
	public BendingNation getEarthNation() {
		return getNation("Earth");
	}
	public BendingNation getWaterNation() {
		return getNation("Water");
	}
	public BendingNation getAirNation() {
		return getNation("Air");
	}
	
	public BendingNation getNation(String name) {
		for (BendingNation nation : nations) {
			if (ChatColor.stripColor(nation.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) return nation;
		}
		return null;
	}
	
	public List<BendingNation> getNations() {
		return nations;
	}
	
	public void registerNation(BendingNation nation) {
		nations.add(nation);
	}
	public void unregisterNation(BendingNation nation) {
		if (nations.contains(nation)) nations.remove(nation);
	}
	public boolean isRegistered(BendingNation nation) {
		return nations.contains(nation);
	}
	
	public boolean playerHasNation(BendingPlayer player) {
		for (BendingNation nation : nations) {
			if (nation.hasPlayer(player) || nation.getLeaderUUID().equalsIgnoreCase(player.getSpigotPlayer().getUniqueId().toString())) return true;
		}
		return false;
	}
	
	public BendingNation getPlayerNation(BendingPlayer player) {
		for (BendingNation nation : nations) {
			if (nation.hasPlayer(player)) return nation;
		}
		return null;
	}
	
	public void clearPlayerNations(BendingPlayer player) {
		for (BendingNation nation : nations) {
			if (nation.hasPlayer(player)) nation.kick(player);
		} 
	}
	
	
	
	/*
	 * Data Handling
	 */
	
	public void saveElementNations() {
		NationManager nm = NationManager.getInstance();
		FileConfiguration nationData = ConfigManager.getInstance().getNationData();
		
		List<String> fireNation = new ArrayList<>();
		for (UUID uuid : nm.getFireNation().getPlayers()) fireNation.add(uuid.toString());
		
		List<String> earthNation = new ArrayList<>();
		for (UUID uuid : nm.getEarthNation().getPlayers()) earthNation.add(uuid.toString());
		
		List<String> waterNation = new ArrayList<>();
		for (UUID uuid : nm.getWaterNation().getPlayers()) waterNation.add(uuid.toString());
		
		List<String> airNation = new ArrayList<>();
		for (UUID uuid : nm.getAirNation().getPlayers()) airNation.add(uuid.toString());
		
		nationData.set("fire-nation.members", fireNation);
		nationData.set("earth-nation.members", earthNation);
		nationData.set("water-nation.members", waterNation);
		nationData.set("air-nation.members", airNation);
		ConfigManager.getInstance().saveNationData();
	}
	
	public void loadElementNations() {
		NationManager nm = NationManager.getInstance();
		FileConfiguration nationData = ConfigManager.getInstance().getNationData();

		int maxCapacity = (int)Setting.NATION_MAX_CAPACITY.get();
		BendingNation fireNation = new BendingNation(ChatColor.RED + "Fire", ChatColor.RED,  maxCapacity, FIRE_LEADER.toString(), BendingItemUtil.getFireElementItem());
		BendingNation earthNation = new BendingNation(ChatColor.GREEN + "Earth", ChatColor.GREEN,  maxCapacity, EARTH_LEADER.toString(), BendingItemUtil.getEarthElementItem());
		BendingNation waterNation = new BendingNation(ChatColor.AQUA + "Water", ChatColor.AQUA,  maxCapacity, WATER_LEADER.toString(), BendingItemUtil.getWaterElementItem());
		BendingNation airNation = new BendingNation(ChatColor.GRAY + "Air", ChatColor.GRAY, maxCapacity, AIR_LEADER.toString(), BendingItemUtil.getAirElementItem());
		
		nm.registerNation(fireNation);
		nm.registerNation(earthNation);
		nm.registerNation(waterNation);
		nm.registerNation(airNation);
		
		if (nationData.getConfigurationSection("fire-nation") != null) {
			//Load Players
			for (String uuid : nationData.getStringList("fire-nation.members")) {
				fireNation.addPlayer(UUID.fromString(uuid));
			}
			for (String uuid : nationData.getStringList("earth-nation.members")) {
				earthNation.addPlayer(UUID.fromString(uuid));
			}
			for (String uuid : nationData.getStringList("water-nation.members")) {
				waterNation.addPlayer(UUID.fromString(uuid));
			}
			for (String uuid : nationData.getStringList("air-nation.members")) {
				airNation.addPlayer(UUID.fromString(uuid));
			}
		}
	}
	
}
