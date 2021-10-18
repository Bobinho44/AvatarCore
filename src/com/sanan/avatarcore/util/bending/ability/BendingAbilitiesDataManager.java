package com.sanan.avatarcore.util.bending.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.fire.FireReversableAbility;
import com.sanan.avatarcore.util.player.PlayerManager;

public class BendingAbilitiesDataManager {

	private static BendingAbilitiesDataManager instance;
	private final AvatarCore ac = AvatarCore.getInstance();
	
	private List<Player> pickupItemWarningPlayers = new ArrayList<Player>();
	private Multimap<Player,Player> deathPlayersPair = ArrayListMultimap.create();
	private List<Map<Location, Material>> quicksandInfectedZone = new ArrayList<Map<Location, Material>>();
	private List<Entity> sinkingPlayers = new ArrayList<Entity>();
	private List<Player> FishingPlayers = new ArrayList<Player>();
	private List<Block> blueFireBlocks = new ArrayList<Block>();
	private Map<Player, Float> chargedDamage = new HashMap<Player, Float>();
	private List<FireReversableAbility> reversableFireAbilities = new ArrayList<FireReversableAbility>();
	private Map<List<Item>, Player> droppedItems = new HashMap<List<Item>, Player>();
	
	public static BendingAbilitiesDataManager getInstance() {
		if (instance == null) {
			instance = new BendingAbilitiesDataManager();
		}
		return instance;
	}
	
	public void addPickupItemWarningPlayer(Player player) {
		pickupItemWarningPlayers.add(player);
	}
	
	public void removePickupItemWarningPlayer(Player player) {
		pickupItemWarningPlayers.remove(player);
	}
	
	public boolean containsPickupItemWarningPlayer(Player player) {
		return pickupItemWarningPlayers.contains(player);
	}
		
	public void addDeathPlayersPair(Player killer, Player victim) {
		deathPlayersPair.put(killer, victim);
	}
	
	public void removeDeathPlayersPair(Player killer, Player victim) {
		deathPlayersPair.remove(killer, victim);
	}
	
	public boolean containsDeathPlayersPair(Player killer, Player victim) {
		return deathPlayersPair.containsEntry(killer, victim);
	}
	
	public void addQuicksandInfectedZone(Map<Location, Material> infectedZone) {
		quicksandInfectedZone.add(infectedZone);
	}
	
	public void removeQuicksandInfectedZone(Map<Location, Material> infectedZone) {
		quicksandInfectedZone.remove(infectedZone);
	}
	
	public List<Player> getPlayersInQuicksandInfectedZone() {
		List<Player> playersInInfectedZone = new ArrayList<Player>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Map<Location, Material> infectedZone : quicksandInfectedZone) {
				for (Location location : infectedZone.keySet()) {
					double x = player.getLocation().getBlockX();
					double z = player.getLocation().getBlockZ();
					if (x == location.getBlockX() && z == location.getBlockZ()) 
						playersInInfectedZone.add(player);
				}
			}
		}
		return playersInInfectedZone;
	}
	
	public Material getInitialQuicksandInfectedZoneBlock(Location location) {
		for (Map<Location, Material> infectedZone : quicksandInfectedZone) {
			for (Entry<Location, Material> map : infectedZone.entrySet()) {
				if (location.equals(map.getKey()))
					return map.getValue();
			}
		}
		return null;
	}
	
	public boolean isInfectedZone(Location location, Map<Location, Material> testedZone) {
		for (Map<Location, Material> infectedZone : quicksandInfectedZone) {
			if (infectedZone.containsKey(location) && !infectedZone.equals(testedZone))
				return true;
		}
		return false;
	}
	
	public void addSinkingPlayer(Entity victim) {
		sinkingPlayers.add(victim);
	}
	
	public void removeSinkingPlayer(Entity victim) {
		sinkingPlayers.remove(victim);
	}
	
	public boolean containsSinkingPlayer(Entity victim) {
		return sinkingPlayers.contains(victim);
	}
	
	public void addFishingPlayer(Player player) {
		FishingPlayers.add(player);
	}
	
	public void removeFishingPlayer(Player player) {
		FishingPlayers.remove(player);
	}
	
	public boolean containsFishingPlayer(Player player) {
		return FishingPlayers.contains(player);
	}
	
	public void addBlueFireBlocks(Block block) {
		blueFireBlocks.add(block);
	}
	
	public void removeBlueFireBlocks(Block block) {
		blueFireBlocks.remove(block);
	}
	
	public void showBlueClue() {
		new BukkitRunnable() {
			public void run() {
				for (Block block : new ArrayList<Block>(blueFireBlocks)) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if(block.getType() == Material.FIRE) {
							player.sendBlockChange(block.getLocation(), Material.SOUL_FIRE.createBlockData());
						}
						else if (block.getType() == Material.CAMPFIRE) {
							player.sendBlockChange(block.getLocation(), Material.SOUL_CAMPFIRE.createBlockData(block.getBlockData().getAsString().replace("minecraft:campfire", "")));
						}
					}
				}
			}
		}.runTaskLaterAsynchronously(ac, 1);
	}
	
	public void hideBlueClue(Player player) {
		for (Block block : new ArrayList<Block>(blueFireBlocks)) {
			player.sendBlockChange(block.getLocation(), block.getBlockData());
		}
	}
	
	public void addChargedDamage(Player player, float damage) {
		chargedDamage.put(player, damage);
	}
	
	public void removeChargedDamage(Player player, float damage) {
		chargedDamage.remove(player);
	}
	
	public float getChargedDamage(Player player) {
		Float damage = chargedDamage.get(player);
		return damage == null || !player.isSneaking() ? 0 : damage;
	}
	
	public void addFireAbility(FireReversableAbility ability) {
		reversableFireAbilities.add(ability);
	}
	
	public void removeFireAbility(FireReversableAbility ability) {
		reversableFireAbilities.remove(ability);
	}
	
	public void reverseFireAbility(Location reverser) {
		for (FireReversableAbility ability : reversableFireAbilities) {
			if (ability.couldReverse(reverser)) {
					ability.reverse();
					return;
			}
		}
	}
	
	public void addDroppedItems(List<Item> items, Player player) {
		droppedItems.put(items, player);
		long time = PlayerManager.getInstance().getBendingPlayer(player).hasFinishTutorial() ? 600 : 9999999;
		new BukkitRunnable() {
		    public void run() {
		    	removeDroppedItems(items, player);
		    }
		}.runTaskLaterAsynchronously(ac, time);
	}
	
	public void removeDroppedItems(List<Item> items, Player player) {
		droppedItems.remove(items, player);
	}
	
	public boolean canPickUpItems(Item item, Player player) {
		Boolean contain = true;
		for (Entry<List<Item>, Player> map : droppedItems.entrySet()) {
			if (map.getKey().contains(item)) {
				contain = false;
				if (map.getValue().equals(player)) {
					return true;
				}
			}
		}
		return contain;
	}
}
