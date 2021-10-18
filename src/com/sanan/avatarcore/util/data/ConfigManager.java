package com.sanan.avatarcore.util.data;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ConfigManager {

	private static ConfigManager instance;
	private static AvatarCore ac = AvatarCore.getInstance();

	/*
	 * Files
	 */
	public static FileConfiguration playerData;
	public static File playerDataFile;
	public static FileConfiguration tribeData;
	public static File tribeDataFile;
	public static FileConfiguration nationData;
	public static File nationDataFile;
	public static FileConfiguration npcData;
	public static File npcDataFile;
	public static FileConfiguration plotData;
	public static File plotDataFile;
	public static FileConfiguration crateData;
	public static File crateDataFile;

	public static ConfigManager getInstance() {
		if (instance == null)
			instance = new ConfigManager();
		return instance;
	}

	private ConfigManager() {
	}

	public void setup() {
		if (!ac.getDataFolder().exists()) {
			ac.getDataFolder().mkdir();
		}
		ac.saveDefaultConfig();
		playerDataFile = new File(ac.getDataFolder(), "playerdata.yml");
		tribeDataFile = new File(ac.getDataFolder(), "tribedata.yml");
		nationDataFile = new File(ac.getDataFolder(), "nationdata.yml");
		npcDataFile = new File(ac.getDataFolder(), "npcdata.yml");
		plotDataFile = new File(ac.getDataFolder(), "plotdata.yml");
		crateDataFile = new File(ac.getDataFolder(), "cratedata.yml");
		
		if(!npcDataFile.exists()) {
			ac.saveResource("npcdata.yml", false);
		}
		if (!playerDataFile.exists()) {
			try {
				playerDataFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The playerdata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the playerdata.yml file");
			}
		}
		if (!tribeDataFile.exists()) {
			try {
				tribeDataFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The tribedata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the tribedata.yml file");
			}
		}
		if (!nationDataFile.exists()) {
			try {
				nationDataFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The nationdata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the nationdata.yml file");
			}
		}
		if (!plotDataFile.exists()) {
			try {
				plotDataFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The plotdata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the plotdata.yml file");
			}
		}
		if (!crateDataFile.exists()) {
			try {
				crateDataFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The cratedata.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the cratedata.yml file");
			}
		}
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
		tribeData = YamlConfiguration.loadConfiguration(tribeDataFile);
		nationData = YamlConfiguration.loadConfiguration(nationDataFile);
		npcData = YamlConfiguration.loadConfiguration(npcDataFile);
		plotData = YamlConfiguration.loadConfiguration(plotDataFile);
		crateData = YamlConfiguration.loadConfiguration(crateDataFile);
	}

	/*
	 * Get, save, reload files
	 */

	public FileConfiguration getPlayerData() {
		return playerData;
	}

	public FileConfiguration getTribeData() {
		return tribeData;
	}

	public FileConfiguration getNationData() {
		return nationData;
	}
	
	public FileConfiguration getNPCData() {
		return npcData;
	}
	
	public FileConfiguration getPlotData() {
		return plotData;
	}
	
	public FileConfiguration getCrateData() {
		return crateData;
	}
	
	public void savePlayerData() {
		try {
			playerData.save(playerDataFile);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "The playerdata.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the playerdata.yml file");
		}
	}

	public void saveTribeData() {
		try {
			tribeData.save(tribeDataFile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The tribedata.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the tribedata.yml file");
		}
	}

	public void saveNationData() {
		try {
			nationData.save(nationDataFile);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "The nationdata.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the nationdata.yml file");
		}
	}
	
	public void saveNPCData() {
		try {
			npcData.save(npcDataFile);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "The npcdata.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the npcdata.yml file");
		}
	}
	
	public void savePlotData() {
		try {
			plotData.save(plotDataFile);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "The plot.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the plot.yml file");
		}
	}
	
	public void saveCrateData() {
		try {
			crateData.save(crateDataFile);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.AQUA + "The crate.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the crate.yml file");
		}
	}
	
	public void reloadPlayerData() {
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The playerdata.yml file has been reload");
	}

	public void reloadTribeData() {
		tribeData = YamlConfiguration.loadConfiguration(tribeDataFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The tribedata.yml file has been reload");
	}

	public void reloadNationData() {
		nationData = YamlConfiguration.loadConfiguration(nationDataFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The nationdata.yml file has been reload");
	}
	
	public void reloadPlotData() {
		plotData = YamlConfiguration.loadConfiguration(plotDataFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The plotdata.yml file has been reload");
	}
	
	public void reloadCrateData() {
		crateData = YamlConfiguration.loadConfiguration(crateDataFile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The cratedata.yml file has been reload");
	}

	public void clearTribeData() {
		for (String key : tribeData.getKeys(false)) {
			tribeData.set(key, null);
		}
		saveTribeData();
	}

	public void clearNationData() {
		for (String key : nationData.getKeys(false)) {
			nationData.set(key, null);
		}
		saveNationData();
	}
	
	public void clearPlayerData() {
		playerDataFile.delete();
		reloadPlayerData();
		savePlayerData();
		
		RegionManager regionsManager = PlotManager.getInstance().getRegions();
		for (Entry<String, ProtectedRegion> regions : PlotManager.getInstance().getRegions().getRegions().entrySet()) {
			 for (BendingNation nation : NationManager.getInstance().getNations()) {
				 if (regions.getKey().matches("^" + ChatColor.stripColor(nation.getName().toLowerCase()) + "-(big|small)-plot-[0-9]*$")) {
					 DefaultDomain owners = new DefaultDomain();
					 owners.removeAll();
					 regions.getValue().setOwners(owners);
					 try {
						regionsManager.save();
					} catch (StorageException e) {}
				 }
			 }
		}
	}
	
	public void clearPlotData() {
		plotDataFile.delete();
		reloadPlotData();
		savePlotData();
	}

	public void clearCrateData() {
		crateDataFile.delete();
		reloadCrateData();
		saveCrateData();
	}
	
	/**
	 * Convert a Location into a save-able String
	 * 
	 * @param loc
	 * @return String
	 */
	public String locationToString(Location loc) {
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getWorld().getName());
		sb.append(",");
		sb.append(loc.getX());
		sb.append(",");
		sb.append(loc.getY());
		sb.append(",");
		sb.append(loc.getZ());

		return sb.toString();
	}

	/**
	 * Converts a String to a Bukkit Location Valid String is formatted as:
	 * worldName,xLocation,yLocation,zLocation
	 * 
	 * @param str String to parse into Bukkit Location
	 * @return Location on Success or null on failure
	 * @error Returns null if String could not be parsed
	 */
	public Location stringToLocation(String str) {
		/**
		 * Location Strings are formatted as: worldName,xLocation,yLocation,zLocation So
		 * we can parse out the relevant information and build a Bukkit Location
		 */

		// If ',' is not contained in the String, we were passed an invalid String, we
		// should fail
		if (!str.contains(","))
			return null;
		String[] parts = str.split(",");

		// If there are not 4 parts we were passed an invalid String and should fail
		if (parts.length != 4)
			return null;

		// If world is invalid we should fail
		World world;
		if ((world = Bukkit.getWorld(parts[0])) == null)
			return null;
		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		double z = Double.parseDouble(parts[3]);

		return new Location(world, x, y, z);
	}

}
