package com.sanan.avatarcore.util.nation.tribe;

import java.util.*;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.google.common.collect.ImmutableSet;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.plot.Plot;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class BendingTribe {
	
	private static final int ROGUE_TRIBE_MAX_PLAYERS = 15;
	public static final int NATION_TRIBE_MAX_PLAYERS = 10;
	private static final int ROGUE_TRIBE_MAX_CLAIMS = 130;
	public static final int NATION_TRIBE_MAX_CLAIMS = 100;
	
	private final ClaimManager clm = ClaimManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	private final TribeManager tm = TribeManager.getInstance();
	private final NationManager nm = NationManager.getInstance();
	
	private String name;
	private Location home = null;
	private int tribePower;
	private List<Claim> claims = new ArrayList<Claim>();
	private List<BendingTribe> allies = new ArrayList<BendingTribe>();
	private boolean leaderIsRogue;
	private Map<UUID, TribeMember> membersLookup = new HashMap<>();
	private List<BendingPlayer> joinRequests = new ArrayList<BendingPlayer>();
	private List<BendingTribe> allyRequest = new ArrayList<BendingTribe>();
	
	/**
	 * Used when creating a new BendingTribe. Sets Tribe Home location to Leader's
	 * current position
	 * 
	 * @param leader
	 * @param name
	 */
	public BendingTribe(BendingPlayer leader, String name) {
		this.name = name;
		this.addMember(leader.getSpigotPlayer().getUniqueId(), TribeRole.LEADER);
		this.tribePower = leader.getPlayerPower().getPowerValue();
		this.leaderIsRogue = !nm.playerHasNation(leader);
	}

	/**
	 * Used for Loading BendingTribe from File
	 * 
	 * @param leader
	 * @param name
	 */
	public BendingTribe(UUID leader, String name) {
		this.name = name;
		this.addMember(leader, TribeRole.LEADER);
	}
	
	public boolean isTheLeaderRogue() {
		return this.leaderIsRogue;
	}
	
	public void theLeaderIsRogue(boolean isRogue) {
		this.leaderIsRogue = isRogue;
	}
	
	public boolean hasAllyRequest(BendingTribe tribe) {
		return allyRequest.contains(tribe);
	}
	
	public void createAlly(BendingTribe ally) {
		allies.add(ally);
		ally.allies.add(this);
		removeAllyRequest(ally);
	}
	
	public void removeAlly(BendingTribe ally) {
		allies.remove(ally);
		ally.allies.remove(this);
	}
	
	public void sendAllyRequest(BendingTribe tribe) {
		tribe.allyRequest.add(this);
		new BukkitRunnable() {
			public void run() {
				removeAllyRequest(tribe);
			}
		}.runTaskLaterAsynchronously(AvatarCore.getInstance(), 600000);
	}
	
	public void removeAllyRequest(BendingTribe ally) {
		if (hasAllyRequest(ally)) {
			ally.allyRequest.remove(this);
		}
	}
	
	public List<BendingTribe> getAllies() {
		return this.allies;
	}
	
	public boolean isAlly(BendingTribe tribe) {
		return allies.contains(tribe);
	}
	
	public boolean isAlly(Player player) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		BendingTribe tribe = tm.getTribe(bPlayer);
		for (BendingTribe ally : getAllies()) {
			if (ally.equals(tribe)) 
				return true;
		}
		return false;
	}
	
	public int getMemberNumber() {
		return this.getMembers().size();
	}
	
	public int getMaxPlayer() {
		return leaderIsRogue ? ROGUE_TRIBE_MAX_PLAYERS : NATION_TRIBE_MAX_PLAYERS;
	}
	
	public List<Claim> getClaims() {
		return claims;
	}
	
	public List<String> getClaimsInfo() {
		List<String> claimsInfo = new ArrayList<String>();
		for (Claim claim : getClaims()) {
			claimsInfo.add(claim.getX() + "/" + claim.getY());
		}
		return claimsInfo;
	}
	
	public int getMaxClaims() {
		int maxClaim = leaderIsRogue ? ROGUE_TRIBE_MAX_CLAIMS : NATION_TRIBE_MAX_CLAIMS;
		return 10 * getMemberNumber() > maxClaim ? maxClaim : 10 * getMemberNumber();
	}
	
	public int getClaimNumber() {
		return getClaims().size();
	}
	
	public void claim(Claim claim) {
		claims.add(claim);	
		clm.claim(claim, this);
	}
	
	public String tryClaim(Claim claim) {
		if (claim.getWorld().equals(Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get()))) {
        	
			if (getClaimNumber() >= getMaxClaims() && getClaimNumber() < (isTheLeaderRogue() ? 130 : 100)) {
				return "NeedMorePlayer";
			}
	        	
    		if (getClaimNumber() >= getMaxClaims()) {
    			return "LimitClaim";
    		}
	         
	        BendingTribe claimOwner = clm.getClaims().get(claim);
	            
	    	if (claimOwner != null && claimOwner.getTribePower() >= claimOwner.getClaimNumber()) {
	    		return "CantClaim";
	    	}
	    	
	    	if (claimOwner != null && claimOwner.equals(this)) {
	    		return "YourClaim";
	    	}
	    	
	    	claim(claim);  
	    	if (claimOwner != null) {
	    		claimOwner.unclaim(claim);
	    		return "From " + claimOwner.getName();
	    	}
	    	return "Claim";
		}
		return "WrongWorld";
	}
	
	public void unclaim(Claim claim) {
		claims.remove(claim);
		clm.unclaim(claim, this);
	}
	
	public void unclaimAll() {
		for (Claim claim : new ArrayList<Claim>(getClaims())) {
			unclaim(claim);
		}
	}
	
	public int getTribePower() {
		return tribePower;
	}

	public int getMaxPower() {
		return leaderIsRogue ? 150 : 100;
	}
	
	protected int recalculateTribePower() {
		int result = 0;
		for (TribeMember member : this.getMembers()) {
			result += pm.getPlayerPower(member.getUuid());
		}
		if (result > getMaxPower()) {
			return getMaxPower();
		}
		return tribePower = result;
	}

	public String getName() {
		return name;
	}

	public Set<TribeMember> getMembers() {
		return ImmutableSet.copyOf(membersLookup.values());
	}

	public TribeMember getMember(UUID uuid) {
		return membersLookup.get(uuid);
	}

	protected void setMembersLookup(Map<UUID, TribeMember> map) {
		membersLookup = map;
	}

	public TribeMember getLeader() {
		for (TribeMember member : this.membersLookup.values()) {
			if (member.getRole() == TribeRole.LEADER) {
				return member;
			}
		}
		return null;
	}

	public TribeRole getMemberRole(UUID player) {
		if (!this.hasMember(player)) {
			return null;
		} 
		else {
			return membersLookup.get(player).getRole();
		}
	}

	public boolean hasMember(UUID player) {
		return membersLookup.containsKey(player);
	}

	public boolean isOfficer(UUID player) {
		return membersLookup.containsKey(player) && membersLookup.get(player).getRole() == TribeRole.OFFICER;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addMember(UUID uuid) {
		addMember(uuid, TribeRole.DEFAULT);
	}

	public void addMember(UUID uuid, TribeRole role) {
		TribeMember tribeMember = new TribeMember(uuid);
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			return;
		}

		tribeMember.setPlayerName(player.getName());
		tribeMember.setRole(role);
		membersLookup.put(uuid, tribeMember);
		tm.addMemberToLookup(player.getName(), this.name);
		recalculateTribePower();
	}

	public void removeMember(UUID uuid) {
		getMember(uuid).activeAutoClaim(false);
		membersLookup.remove(uuid);
		tm.removeMemberFromLookup(Bukkit.getPlayer(uuid).getName());
		recalculateTribePower();
	}

	public void promote(UUID uuid) {
		if (hasMember(uuid)) {
			TribeMember member = membersLookup.get(uuid);
			member.setRole(member.getRole().getHigherLevel());
		}
	}
	
	public void demote(UUID uuid) {
		if (hasMember(uuid)) {
			TribeMember member = membersLookup.get(uuid);
			member.setRole(member.getRole().getLowerLevel());
			getMember(uuid).activeAutoClaim(false);
		}
	}

	public void sendTribeMessage(String message) {
		for (UUID uuid : membersLookup.keySet()) {
			if (Bukkit.getServer().getPlayer(uuid) != null)
				Bukkit.getServer().getPlayer(uuid).sendMessage(message);
		}
	}

	public void sendJoinRequest(BendingPlayer player) {
		if (getMemberNumber() < getMaxPlayer() && !player.hasJoinRequest(this)) {
			player.createJoinRequest(this);
			new BukkitRunnable() {
				public void run() {
					removeJoinRequest(player);
				}
			}.runTaskLaterAsynchronously(AvatarCore.getInstance(), 12000);
		}
	}
	
	public void removeJoinRequest(BendingPlayer player) {
		if (player.hasJoinRequest(this)) {
			player.declineJoinRequest(this);;
		}
	}
	
	/*
	 * Invite Request
	 */
	public void acceptJoinRequest(BendingPlayer player) {
		if (hasJoinRequest(player)) {
			joinRequests.remove(player);
			addMember(player.getSpigotPlayer().getUniqueId());
		}
	}
	
	public void declineJoinRequest(BendingPlayer player) {
		if (hasJoinRequest(player)) {
			joinRequests.remove(player);
		}
	}
	
	public void createJoinRequest(BendingPlayer player) {
		this.joinRequests.add(player);
	}
	public boolean hasJoinRequest(BendingPlayer player) {
		return getJoinRequests().contains(player);
	}
	
	public List<BendingPlayer> getJoinRequests() {
		return joinRequests;
	}

	public void disband() throws NoLoanPermittedException, ArithmeticException, StorageException, UserDoesNotExistException {
		for (BendingTribe ally : getAllies()) {
	    	ally.removeAlly(this);
		}
		Player player = Bukkit.getPlayer(getLeader().getUuid());
		Plot playerPlot = PlotManager.getInstance().getPlayerPlot(player);
		if (playerPlot != null) 
			playerPlot.sell(player);
		unclaimAll();
		membersLookup.clear();
		tm.unregisterTribe(this);
	}

	/**
	 * Return the home spawn location for this Tribe
	 * 
	 * @return home Location
	 */
	public Location getHomeLocation() {
		return this.home;
	}

	/**
	 * Set this Tribe's Home to specified Location
	 * 
	 * @param Location
	 */
	public void setHomeLocation(Location loc) {
		this.home = loc;
	}
	
	/**
	 * Checks if a HomeLocation is set for this Tribe
	 * @return boolean true if home is set, false if no home
	 */
	public boolean hasHomeLocation() {
		return home != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BendingTribe)) {
			return false;
		}
		BendingTribe other = (BendingTribe) obj;
		return other.getName().equalsIgnoreCase(getName());
	}
}
