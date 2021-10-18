package com.sanan.avatarcore.util.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.util.player.level.BendingLevel;
import com.sanan.avatarcore.util.player.level.BendingLevel.LevelClass;
import com.sanan.avatarcore.util.player.nation.PlayerNationData;
import com.sanan.avatarcore.util.player.tutorial.BendingTutorial;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.data.ConfigManager;

public class PlayerManager extends BukkitRunnable {

	private static PlayerManager instance = null;
	private final ConfigManager cm = ConfigManager.getInstance();
	private final BendingAbilityManager bam = BendingAbilityManager.getInstance();
	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	private static List<BendingPlayer> players = new ArrayList<>();
	
	public static PlayerManager getInstance() {
		if (instance == null) instance = new PlayerManager();
		return instance;
	}
	
	public List<BendingPlayer> getPlayers() {
		return players;
	}
	
	public void registerPlayer(BendingPlayer player) {
		if (!players.contains(player)) players.add(player);
	}
	
	public void unregisterPlayer(BendingPlayer player) {
		players.remove(player);
	}
	
	public boolean playerRegistered(BendingPlayer player) {
		return players.contains(player);
	}
	
	public BendingPlayer createRegisterBendingPlayer(Player player) {
		BendingPlayer newBPlayer = new BendingPlayer(player);
		registerPlayer(newBPlayer);
		return newBPlayer;
	}
	
	public BendingPlayer getBendingPlayer(Player player) {
		for (BendingPlayer bPlayer : players) {
			if (bPlayer.getSpigotPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) return bPlayer;
		}
		return null;
	}
	
	/*
	 * Data handling
	 */
	public boolean playerDataExists(Player player) {
		return cm.getPlayerData().getConfigurationSection(player.getUniqueId().toString()) != null;
	}
	
	//Save player data to file
	public void savePlayerData(BendingPlayer player) {
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".hotbars", null);

		player.getPlayerNationData().store(player);
		List<String> elements = new ArrayList<>();
		for (BendingElement element : player.getElements()) {
			elements.add(element.toString());
		}
		
		//Hotbars
		for (BendingHotbar hotbar : player.getHotbars()) {
			String name = hotbar.getName();
			List<String> abilities = new ArrayList<>();
			
			for (int i = 0; i < 9; i++) {
				BendingAbility ability = hotbar.getAbilities()[i];
				if (ability == null) {
					abilities.add("");
				} else {
					abilities.add(ability.getName().toLowerCase());
				}
			}
			
			cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".hotbars." + name, abilities);
		}
		
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".elements", elements);
		
		//Chi
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".chi", player.getChi().getMaximum());
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".power", player.getPlayerPower().getPowerValue());
		
		//Level
		for (HashMap.Entry<String, Integer> level : player.getBendingLevel().getBendingLevel().entrySet()) {
			cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".level." + level.getKey() + ".level", level.getValue());
		}
		for (HashMap.Entry<String, Integer> xp : player.getBendingLevel().getBendingXp().entrySet()) {
			cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".level." + xp.getKey() + ".xp", xp.getValue());
		}
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".level.class", player.getBendingLevel().getBendingClass());
		
		List<String> passiveAbilityActivated = new ArrayList<String>();
		for (BendingAbility ability : player.getPassiveAbilityActivated()) {
			passiveAbilityActivated.add(ability.getName().toLowerCase());
		}
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".passiveAbility" , passiveAbilityActivated);
		cm.getPlayerData().set(player.getSpigotPlayer().getUniqueId().toString() + ".tutorial", player.getTutorialStep());
		cm.savePlayerData();
	}
	
	//Load player data from file
	public void loadPlayerData(Player player) {
				
		FileConfiguration playerData = cm.getPlayerData();
		final ConfigurationSection playerDataSection = playerData.getConfigurationSection(player.getUniqueId().toString());
		if (playerDataSection == null) {
			return;
		}

		final List<String> elements = playerDataSection.getStringList("elements");
		final int chi = playerDataSection.getInt("chi");
		final List<BendingElement> bendingElements = new ArrayList<>();
		for (String element : elements) {
			bendingElements.add(BendingElement.fromString(element));
		}

		final PlayerNationData playerNationData = PlayerNationData.loadFromFile(player.getUniqueId(), playerData);
		
		final ArrayList<HashMap<String, Integer>> bendingLevelInfo = new ArrayList<HashMap<String, Integer>>(Arrays.asList(new HashMap<String, Integer>(),new HashMap<String, Integer>()));
		for (LevelClass element : BendingLevel.LevelClass.values()) {
			bendingLevelInfo.get(0).put(element.toString().toLowerCase(), playerDataSection.getInt("level." + element.toString().toLowerCase() + ".xp"));
			bendingLevelInfo.get(1).put(element.toString().toLowerCase(), playerDataSection.getInt("level." + element.toString().toLowerCase().toLowerCase() + ".level"));
		}
		
		String bendingClass = playerDataSection.getString("level.class");
		bendingClass = bendingClass == null ? "none" : bendingClass;
		
		final List<BendingAbility> passiveAbility = new ArrayList<>();
		for (String ability : playerData.getStringList(player.getUniqueId().toString() + ".passiveAbility")) {
			passiveAbility.add(bam.getAbility(ability));
		}
		BendingPlayer bPlayer = new BendingPlayer(player, bendingElements, getPlayerHotbarsFromData(player), chi, bendingLevelInfo, bendingClass, passiveAbility);
		
		bPlayer.setPlayerNationData(playerNationData);
		bPlayer.setPlayerPower(getPlayerPower(player.getUniqueId()));
		registerPlayer(bPlayer);
		for (BendingAbility ability : passiveAbility) {
			PassiveAbility.createPassiveAbilityInstance(bPlayer, ability);
		}
		double tutorialStep = playerData.getDouble(player.getUniqueId().toString() + ".tutorial", -1);
		if (tutorialStep < 9 && tutorialStep > -1)
		bPlayer.startTutorial(tutorialStep);
	}
	
	public int getPlayerPower(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			BendingPlayer bPlayer = getBendingPlayer(player);
			if (bPlayer != null) return bPlayer.getPlayerPower().getPowerValue();
		}
		return cm.getPlayerData().getConfigurationSection(uuid.toString()).getInt("power", 10);
	}
	
	//Helper to load and get player hotbars
	private List<BendingHotbar> getPlayerHotbarsFromData(Player player) {
		FileConfiguration playerData = cm.getPlayerData();
		BendingAbilityManager bam = BendingAbilityManager.getInstance();
		List<BendingHotbar> hotbars = new ArrayList<>();
		
		if (playerData.getConfigurationSection(player.getUniqueId().toString() + ".hotbars") == null) return hotbars;
		
		for (String name : playerData.getConfigurationSection(player.getUniqueId().toString() + ".hotbars").getKeys(false)) {
			List<String> abilities = playerData.getStringList(player.getUniqueId().toString() + ".hotbars." + name);
			BendingHotbar hotbar = new BendingHotbar(name);
			for (int i = 0; i < 9; i++) {
				hotbar.setAbility(i, bam.getAbility(abilities.get(i)));
			}
			hotbars.add(hotbar);
		}
		return hotbars;
	}
	
	public void give(Player player, List<ItemStack> items) {
		Collection<ItemStack> toDrop = new ArrayList<ItemStack>();
		if (getBendingPlayer(player).isInBendingMode()) {
			toDrop = items;
		} else {
			toDrop = (player.getInventory().addItem(items.toArray(new ItemStack[0]))).values();
		}
		List<Item> dropped = new ArrayList<Item>();
		for (ItemStack item : toDrop) {
			Item drop = player.getWorld().dropItemNaturally(player.getLocation(), item);
			drop.setCanMobPickup(false);
			if (!getBendingPlayer(player).hasFinishTutorial()) {
				drop.setPersistent(true);
				drop.setCustomName(player.getName());
				BendingTutorial.clearEntity(player, drop.getEntityId());
			}
			dropped.add(drop);
		}
		badm.addDroppedItems(dropped, player);
	}

	@Override
	public void run() {
		for (BendingPlayer bPlayer : players) {
			if (bPlayer.isInBendingMode() && !bam.hasAbilityActive(bPlayer)) {
				bPlayer.getChi().regenerate(1);
			}
			bPlayer.updateBendingChiBar();
		}
	}
	
}
