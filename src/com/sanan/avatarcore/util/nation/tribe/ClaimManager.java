package com.sanan.avatarcore.util.nation.tribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.sanan.avatarcore.util.player.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public class ClaimManager {

	private static ClaimManager instance;
	private Map<Claim, BendingTribe> claims = new HashMap<>();
	
	
	public static ClaimManager getInstance() {
		if (instance == null) {
			instance = new ClaimManager();
		}
		return instance;
	}
	
	public Map<Claim, BendingTribe> getClaims() {
		return this.claims;
	}
	public void claim(Claim claim, BendingTribe tribe) {
		claims.put(claim, tribe);
	}
	
	public void unclaim(Claim claim, BendingTribe tribe) {
		if (claims.get(claim).equals(tribe)) {
			claims.remove(claim);
		}
		if (tribe.getHomeLocation() != null && claim.equals(tribe.getHomeLocation().getChunk())) {
			tribe.setHomeLocation(null);
		}
	}
	
	public String[] getMap(Player player) {
		claims.put(new Claim(0, 4, player.getWorld()), new BendingTribe(PlayerManager.getInstance().getBendingPlayer(player), "HEHO"));
		int initialI = player.getLocation().getChunk().getZ() - 7;
		int initialJ = player.getLocation().getChunk().getX() - 19;
		List<String> map = new ArrayList<String>(Arrays.asList(ChatColor.GOLD + "------------- Tribes 's Map --------------"));
		List<BendingTribe> tribes = new ArrayList<BendingTribe>();
		String tribesInfo = "";
		for (int i = 0; i < 15; i++) {
			String line = "";
			for (int j = 0; j < 39; j++) {
				if (i >= 3 || j >= 3) {
					BendingTribe claimOwner = claims.get(new Claim(initialJ + j, initialI + i, player.getWorld()));
					if (i == 7 && j == 19) {
						line += ChatColor.AQUA + "+" + ChatColor.RESET;
					}
					else if (claimOwner != null) {
						if (!tribes.contains(claimOwner)) {
							tribes.add(claimOwner);
							tribesInfo += (char) (64 + tribes.indexOf(claimOwner)) + ": " + claimOwner.getName() + " ";
						}
						line += (char) (64 + tribes.indexOf(claimOwner));
					}
					else {
						line += "-";
					}
				} 
			}
			map.add(line);
		}
		String[] cardinalPoints = getDirection(player);
		for (int k = 1; k < 4; k++) {
			map.set(k, cardinalPoints[k-1] + map.get(k));
		}
		map.add(tribesInfo);
		return map.toArray(new String[0]);
	}
	
	private String[] getDirection(Player player) {
		String[] cardinalPoints = {"§6\\N/§r", "§6W+E§r", "§6/S\\§r"};
		switch (player.getFacing()) {
		case NORTH:
			cardinalPoints[0] = cardinalPoints[0].replace("N", "§cN§6");
			break;
		case WEST:
			cardinalPoints[1] = cardinalPoints[1].replace("W", "§cW§6");
			break;
		case EAST:
			cardinalPoints[1] = cardinalPoints[1].replace("E", "§cE§6");
			break;
		case SOUTH:
			cardinalPoints[2] = cardinalPoints[2].replace("S", "§cS§6");
			break;
		default:
			break;
		}
		return cardinalPoints;
	}

}
