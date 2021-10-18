package com.sanan.avatarcore.util.plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;

public class PlotManager {
	
	private static PlotManager instance;
	
	private final World world = Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get());
	private final com.sk89q.worldedit.world.World rgWorld = BukkitAdapter.adapt(world);
	private final RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(rgWorld);
	private final ConfigManager cm = ConfigManager.getInstance();
	
	private Map<String, Plot> PLOT_INSTANCE = new HashMap<>();
	
	public static PlotManager getInstance() {
		if (instance == null) {
			instance = new PlotManager();
		}
		return instance;
	}
	
	public RegionManager getRegions() {
		return this.regions;
	}
	
	public Map<String, Plot> getAllPlot() {
		return this.PLOT_INSTANCE;
	}
	
	public List<Plot> getAllNationPlot(String nation) {
		List<Plot> plots = new ArrayList<Plot>();
		for (Map.Entry<String, Plot> plot : PLOT_INSTANCE.entrySet()) {
			if (plot.getKey().split("-")[0].equalsIgnoreCase(nation)) {
				plots.add(plot.getValue());
			}
		}
		Comparator<Plot> comparator = new Comparator<Plot>() {   
			@Override
			public int compare(Plot plot1, Plot plot2) {
				String[] plotName1 = plot1.getName().split(" ");
				String[] plotName2 = plot2.getName().split(" ");
				if (plotName1[1].equalsIgnoreCase(plotName2[1])) {
					return Integer.valueOf(plotName1[3]) - Integer.valueOf(plotName2[3]);
				}
				return plotName1[1].equalsIgnoreCase("Big") ? 1 : -1;
			}
		};
		Collections.sort(plots, comparator);
		return plots;
	}
	
	public Plot getPlot(String plotName) {
		for (Map.Entry<String, Plot> plot : PLOT_INSTANCE.entrySet()) {
			if (plot.getKey().equalsIgnoreCase(plotName)) {
				return plot.getValue();
			}
		}
		return null;
	}
	
	public Plot getPlayerPlot(Player player) {	
		for (Map.Entry<String, Plot> plot : PLOT_INSTANCE.entrySet()) {
			if (plot.getValue().getOwner() != null ) {
				if (plot.getValue().getOwner().getUniqueId().equals(player.getUniqueId())) {
					return plot.getValue();
				}
			}
		}
		return null;
	}

	//MODEL PLOT NAME: Earth-Plot-Big-1
	public void registerAllPlot() {
		 for (Entry<String, ProtectedRegion> region : regions.getRegions().entrySet()) {
			 for (BendingNation nation : NationManager.getInstance().getNations()) {
				 if (region.getKey().matches("^" + ChatColor.stripColor(nation.getName().toLowerCase()) + "-(big|small)-plot-[0-9]*$")) {
					
					 Plot plot = null;
					 
					 String[] plotName = region.getKey().split("-");
					 String nationPlot = plotName[0].substring(0, 1).toUpperCase() + plotName[0].substring(1);
					 
					 BlockVector3[] plotCoord = {region.getValue().getMaximumPoint(), region.getValue().getMinimumPoint()};
					 
					 String owners = region.getValue().getOwners().getUniqueIds().toString().replace("[", "").replace("]", "");
					 OfflinePlayer owner = owners.equalsIgnoreCase("") ? null : Bukkit.getOfflinePlayer(UUID.fromString(owners));
					 
					 Location plotHome = new Location(world, plotCoord[0].getX(), 255, plotCoord[0].getZ());
					 String plotHomeString = cm.getPlotData().getString(region.getKey());
					 if (plotHomeString != null)
						 plotHome = cm.stringToLocation(plotHomeString);
					 
					 if (region.getKey().contains("big")) {
						 plot = new Plot(owner, nationPlot + " Big Plot " + plotName[3], 1000000, plotHome, plotCoord);
					 }
					 else {
						 plot = new Plot(owner, nationPlot + " Small Plot " + plotName[3], 500000, plotHome, plotCoord);
					 }
					 PLOT_INSTANCE.put(region.getKey(), plot);
				 }
			 }
		 }
	}
}
