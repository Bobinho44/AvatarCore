package com.sanan.avatarcore;

import co.aikar.commands.PaperCommandManager;

import com.sk89q.worldguard.protection.regions.RegionContainer;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.listeners.PlayerDeathListener;
import com.sanan.avatarcore.listeners.PlotListener;
import com.sanan.avatarcore.listeners.TribeListener;
import com.sanan.avatarcore.listeners.TutorialListener;
import com.sanan.avatarcore.listeners.WallListener;
import com.sanan.avatarcore.listeners.command.NationLeaveListener;
import com.sanan.avatarcore.listeners.CrateListener;
import com.sanan.avatarcore.listeners.DisableReloadListener;
import com.sanan.avatarcore.listeners.LevelListener;
import com.sanan.avatarcore.listeners.NPCListener;
import com.sanan.avatarcore.listeners.ObsidianListener;
import com.sanan.avatarcore.listeners.PacketListener;
import com.sanan.avatarcore.listeners.bending.BendingAbilityListener;
import com.sanan.avatarcore.listeners.bending.BendingModeListener;
import com.sanan.avatarcore.listeners.command.TribeDisbandListener;
import com.sanan.avatarcore.listeners.data.PlayerDataListener;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.crate.BendingCrateManager;
import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.npc.NPCManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.player.power.PowerChargingTask;
import com.sanan.avatarcore.util.plot.Plot;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sanan.avatarcore.commands.CrateCommand;
import com.sanan.avatarcore.commands.LevelCommand;
import com.sanan.avatarcore.commands.NationCommand;
import com.sanan.avatarcore.commands.PlotCommand;
import com.sanan.avatarcore.commands.TribeCommand;
import com.sanan.avatarcore.commands.TutorialCommand;

public class AvatarCore extends JavaPlugin {

	private static AvatarCore instance;
	private static NationManager nm;
	private static TribeManager tm;
	public static PlayerManager pm;
	public static ConfigManager cm;
	public static RegionContainer container;
	
	public static AvatarCore getInstance() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		nm = NationManager.getInstance();
		tm = TribeManager.getInstance();
		pm = PlayerManager.getInstance();
		cm = ConfigManager.getInstance();
		
		cm.setup();
		
		//Load file data
		nm.loadElementNations();
		tm.loadTribesFromFile();
		
		registerListeners();
		registerCommands();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading bending abilities...");
		BendingAbilityManager.getInstance().registerAllPluginAbilities();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Bending abilities loaded successfully!");
		
		BendingAbilityManager.getInstance().runTaskTimer(this, 0, 1);
		PlayerManager.getInstance().runTaskTimer(this, 0, 2);
		new PowerChargingTask().runTaskTimerAsynchronously(this, 20L, 20L * 60L * 5L);
		
		PacketListener.createUseEntityPacketListener();
		PlotManager.getInstance().registerAllPlot();
		NPCManager.getInstance().registerAllNPC();
		BendingCrateManager.getInstance().registerAllCrateCollections();
		new BukkitRunnable() {
	        public void run() {
	        	tm.saveTribesToFile();
	    		nm.saveElementNations();
	    		cm.savePlotData();
	        }
	    }.runTaskTimerAsynchronously(this, 72000, 72000);
	}
	
	public void onDisable() {
		tm.saveTribesToFile();
		nm.saveElementNations();
		cm.savePlotData();
		for (BendingPlayer bPlayer : pm.getPlayers()) {
			if (bPlayer.isInBendingMode()) bPlayer.toggleBendingMode();
			pm.savePlayerData(bPlayer);
		}
		for (Map.Entry<String, Plot> plot : PlotManager.getInstance().getAllPlot().entrySet()) {
			Location plotHome = plot.getValue().getHome();
			cm.getPlotData().set(plot.getKey(), plotHome == null ? "none" : cm.locationToString(plotHome));
		}
		BendingCrateManager.getInstance().saveAllCrateCollections();
	}
	
	private void registerListeners() {
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDataListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new TribeDisbandListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BendingModeListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BendingAbilityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DisableReloadListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new NationLeaveListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlotListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new TribeListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WallListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LevelListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CrateListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new TutorialListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ObsidianListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), this);
	}
	
	private void registerCommands() {
		final PaperCommandManager commandManager = new PaperCommandManager(this);
		commandManager.registerCommand(new NationCommand());
		commandManager.registerCommand(new TribeCommand());
		commandManager.registerCommand(new LevelCommand());
		commandManager.registerCommand(new PlotCommand());
		commandManager.registerCommand(new CrateCommand());
		commandManager.registerCommand(new TutorialCommand());
	}
	
}
