package com.sanan.avatarcore.util.plot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.util.data.Setting;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;

public class Plot {

	private final PlotManager plm = PlotManager.getInstance();
	private final WorldGuardPlugin wg = WorldGuardPlugin.inst();
	
	private OfflinePlayer owner;
	private String plotName;
	private BigDecimal plotPrice;
	private Location plotHome;
	private BlockVector3[] plotCoord;
	private List<Player> inviteMember;
	
	public Plot(OfflinePlayer owner, String plotName, int plotPrice, Location plotHome, BlockVector3[] plotCoord) {
		this.owner = owner;
		this.plotName = plotName;
		this.plotPrice = BigDecimal.valueOf(plotPrice);
		this.plotHome = plotHome;
		this.plotCoord = plotCoord;
		this.inviteMember = new ArrayList<Player>();
	}
	
	public void invite(Player player) {
		inviteMember.add(player);
	}
	
	public void unInvite(Player player) {
		inviteMember.remove(player);
	}
	
	public boolean hasAnInvite(Player player) {
		return inviteMember.contains(player);
	}
	
	public OfflinePlayer getOwner() {
		return this.owner;
	}
	
	public List<OfflinePlayer> getMembers() {
		List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
		RegionManager regionsManager = plm.getRegions();
		ProtectedRegion region = regionsManager.getRegion(ChatColor.stripColor((plotName.replaceAll(" ", "-")).toLowerCase()));
		region.getMembers().getUniqueIds().forEach(member -> members.add(Bukkit.getOfflinePlayer(member)));
		return members;
	}
	
	public String getName() {
		return this.plotName;
	}
	
	public BigDecimal getPrice() {
		return this.plotPrice;
	}
	
	public BlockVector3[] getCoordinates() {
		return this.plotCoord;
	}
	
	public Location getHome() {
		if (this.plotHome.getY() == 255) {
			while (this.plotHome.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR)) {
				 this.plotHome.add(0, -1, 0);
			 }
		}
		return this.plotHome;
	}
	
	public boolean setHome(Location plotHome) {
		BlockVector3 center = (plotCoord[0].add(plotCoord[1])).divide(2);
		double diffX = Math.abs(center.getX() - plotCoord[0].getX());
		double diffZ = Math.abs(center.getZ() - plotCoord[0].getZ());
		
		if (Math.abs(center.getX()-plotHome.getX()) > diffX || Math.abs(center.getZ()-plotHome.getZ()) > diffZ)
			return false;
		
		this.plotHome = plotHome;
		return true;
	}
	
	public void addMember(Player guest) throws StorageException {
		RegionManager regionsManager = plm.getRegions();
		ProtectedRegion region = regionsManager.getRegion(ChatColor.stripColor((plotName.replaceAll(" ", "-")).toLowerCase()));
		DefaultDomain guests = new DefaultDomain();
		guests.addPlayer(wg.wrapPlayer(guest));
		region.setMembers(guests);
		regionsManager.save();
		inviteMember.remove(guest);
	}
	
	public void removeMember(Player guest) throws StorageException {
		RegionManager regionsManager = plm.getRegions();
		ProtectedRegion region = regionsManager.getRegion(ChatColor.stripColor((plotName.replaceAll(" ", "-")).toLowerCase()));
		region.getMembers().removePlayer(wg.wrapPlayer(guest));
		regionsManager.save();
	}
	
	public void buy(Player player) throws StorageException, NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
		UUID uuid = player.getUniqueId();
		if (!(plm.getPlayerPlot(player) != null && owner != null && Economy.getMoneyExact(uuid).compareTo(plotPrice) < 0)) {
			
			RegionManager regionsManager = plm.getRegions();
			ProtectedRegion region = regionsManager.getRegion(ChatColor.stripColor((plotName.replaceAll(" ", "-")).toLowerCase()));
			DefaultDomain owners = new DefaultDomain();
			
			owners.addPlayer(wg.wrapPlayer(player));
			region.setOwners(owners);
			regionsManager.save();
			
			Economy.subtract(uuid, plotPrice);				
			owner = player;
		}
	}
	
	public void sell(Player player) throws StorageException, NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
		UUID uuid = owner.getUniqueId();
		if (player.getUniqueId().equals(uuid)) {
			
			RegionManager regionsManager = plm.getRegions();
			ProtectedRegion region = regionsManager.getRegion(ChatColor.stripColor((plotName.replaceAll(" ", "-")).toLowerCase()));
			
			region.getOwners().clear();
			region.getMembers().clear();
			regionsManager.save();
			
			Economy.add(uuid, plotPrice.divide(BigDecimal.valueOf(2)));
			owner = null;
			plotHome = new Location(Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get()), plotCoord[0].getX(), 255, plotCoord[0].getZ());
		}
	}
}