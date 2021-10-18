package com.sanan.avatarcore.util.nation.tribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class TribeManager {

	private static TribeManager instance = null;
	private static ConfigManager cm = ConfigManager.getInstance();

	private static List<BendingTribe> tribes;
	private final Map<String, String> lookupByMember = new HashMap<>();

	public static TribeManager getInstance() {
		if (instance == null)
			instance = new TribeManager();
		return instance;
	}

	private TribeManager() {
		tribes = new ArrayList<>();
	}

	protected void addMemberToLookup(String playerName, String tribeName) {
		this.lookupByMember.put(playerName, tribeName);
	}

	protected void removeMemberFromLookup(String playerName) {
		this.lookupByMember.remove(playerName);
	}

	public BendingTribe lookupTribeByMember(String memberName) {
		return this.getTribe(this.lookupByMember.get(memberName));
	}

	public void registerTribe(BendingTribe tribe) {
		tribes.add(tribe);
	}

	public void unregisterTribe(BendingTribe tribe) {
		tribes.remove(tribe);
	}

	public boolean isRegistered(BendingTribe tribe) {
		return tribes.contains(tribe);
	}

	public boolean isRegistered(String tribeName) {
		for (BendingTribe tribe : tribes) {
			if (tribe.getName().equalsIgnoreCase(tribeName))
				return true;
		}
		return false;
	}

	public boolean playerHasTribe(UUID player) {
		for (BendingTribe tribe : tribes) {
			if (tribe.hasMember(player))
				return true;
		}
		return false;
	}

	public boolean playerHasTribe(BendingPlayer player) {
		return playerHasTribe(player.getSpigotPlayer().getUniqueId());
	}

	public BendingTribe getTribe(UUID player) {
		for (BendingTribe tribe : tribes) {
			if (tribe.hasMember(player))
				return tribe;
		}
		return null;
	}

	public BendingTribe getTribe(BendingPlayer player) {
		return getTribe(player.getSpigotPlayer().getUniqueId());
	}

	public BendingTribe getTribe(String tribeName) {
		for (BendingTribe tribe : tribes) {
			if (tribe.getName().equalsIgnoreCase(tribeName))
				return tribe;
		}
		return null;
	}

	/*
	 * Load/save file data
	 */
	public void loadTribesFromFile() {
		for (String strTribe : cm.getTribeData().getKeys(false)) {
			Map<UUID, TribeMember> membersMap = new HashMap<>();
			UUID leader = null;
			for (String memberString : cm.getTribeData().getStringList(strTribe + ".members")) {
				String[] splitted = memberString.split(":");
				UUID uuid = UUID.fromString(splitted[0]);
				TribeRole role = TribeRole.match(splitted[1]);
				TribeMember member = new TribeMember(uuid);
				member.setRole(role);

				if (role == TribeRole.LEADER) {
					leader = uuid;
				}

				membersMap.put(uuid, member);
			}
			
			BendingTribe tribe = new BendingTribe(leader, strTribe);
			String location = cm.getTribeData().getString(strTribe + ".location");
			if (!location.equals("none"))
				tribe.setHomeLocation(cm.stringToLocation(location));
			tribe.setMembersLookup(membersMap);
			tribe.theLeaderIsRogue(cm.getTribeData().getBoolean(strTribe + ".leaderIsRogue"));
			registerTribe(tribe);
			tribe.recalculateTribePower();
			
			for (String claimString : cm.getTribeData().getStringList(strTribe + ".claims")) {
				int[] claimsInfo = Arrays.stream(claimString.split("/")).mapToInt(Integer::parseInt).toArray();
				tribe.claim(new Claim(claimsInfo[0], claimsInfo[1], Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get())));
			}
		}
		for (String strTribe : cm.getTribeData().getKeys(false)) {
			BendingTribe tribe = getTribe(strTribe);
			for (String allyString : cm.getTribeData().getStringList(strTribe + ".allies")) {
				tribe.createAlly(getTribe(allyString));
			}
		}
	}

	public void saveTribesToFile() {
		for (BendingTribe tribe : tribes) {
			List<String> membersArray = new ArrayList<>();
			for (TribeMember member : tribe.getMembers()) {
				membersArray.add(member.getUuid().toString() + ":" + member.getRole().toString());
			}

			cm.getTribeData().set(tribe.getName() + ".members", membersArray);

			String homeLocation = tribe.getHomeLocation() == null ? "none" : cm.locationToString(tribe.getHomeLocation());
			cm.getTribeData().set(tribe.getName() + ".location", homeLocation);
			cm.getTribeData().set(tribe.getName() + ".claims", tribe.getClaimsInfo());
			List<String> alliesInfo = new ArrayList<>();
			for (BendingTribe ally : tribe.getAllies()) {
				alliesInfo.add(ally.getName());
			}
			cm.getTribeData().set(tribe.getName() + ".allies", alliesInfo);
			cm.getTribeData().set(tribe.getName() + ".leaderIsRogue", tribe.isTheLeaderRogue());
		}
		cm.saveTribeData();
	}

}
