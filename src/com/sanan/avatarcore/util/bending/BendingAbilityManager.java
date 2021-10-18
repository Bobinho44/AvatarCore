package com.sanan.avatarcore.util.bending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.reflection.AbilityClassLoader;

import net.md_5.bungee.api.ChatColor;

public class BendingAbilityManager extends BukkitRunnable {

	private static BendingAbilityManager instance;
	
	private final Map<String, BendingAbility> ABILITIES_BY_NAME = new HashMap<>();
	private final List<BendingAbility> ABILITY_INSTANCES = new ArrayList<>();
	
	public static BendingAbilityManager getInstance() {
		if (instance == null) {
			instance = new BendingAbilityManager();
		}
		return instance;
	}
	
	public ArrayList<BendingAbility> getAllLoadedAbilities(boolean getPassive) {
		ArrayList<BendingAbility> abilities = new ArrayList<BendingAbility>();
		for (BendingAbility ability : ABILITIES_BY_NAME.values()) {
			if (ability instanceof PassiveAbility == getPassive) {
				abilities.add(ability);
			}
		}
		Comparator<BendingAbility> comparator = new Comparator<BendingAbility>() {
		    @Override
		    public int compare(BendingAbility left, BendingAbility right) {
		        return left.getLevelRequired() - right.getLevelRequired();
		    }
		};
		Collections.sort(abilities, comparator);
		return abilities;
	}
	
	public void cleanupPlayerAbilities(BendingPlayer bPlayer) {
		for (BendingAbility ability : getAllCurrentPlayerAbilities(bPlayer)) ability.remove();
	}
	
	public BendingAbility getPlayerAbility(BendingPlayer bPlayer, String abilityName) {
		for (BendingAbility ability : ABILITY_INSTANCES) {
			if (ability.getPlayer().equals(bPlayer.getSpigotPlayer()) && ability.getName().equalsIgnoreCase(abilityName)) return ability;
		}
		return null;
	}
	
	public List<String> getPlayerUnlockedAbility(BendingPlayer bPlayer, String bendingClass, boolean getPassive) {
		List<String> abilities = new ArrayList<>();
		for (BendingAbility ability : getAllLoadedAbilities(getPassive)) {
			if (ability.getElement().getName().toLowerCase().contains(bendingClass) && ability.getLevelRequired() <= bPlayer.getLevel(bendingClass)) {
				if (!(ability.getName().equalsIgnoreCase("earth levitation") && bPlayer.getLevel("earth") >= 55)) {
					abilities.add(ChatColor.YELLOW + ability.getName());
				}
			}
		}
		return abilities;
	}
	
	public boolean isPlayerUsingAbility(BendingPlayer bPlayer, String abilityName) {
		for (BendingAbility ability : ABILITY_INSTANCES) {
			if (ability.getPlayer().equals(bPlayer.getSpigotPlayer()) && ability.getName().equalsIgnoreCase(abilityName) && !((CoreBendingAbility) ability). isStopped()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAbilityActive(BendingPlayer bPlayer) {
		for (BendingAbility ability : getAllCurrentPlayerAbilities(bPlayer)) {
			if (!(ability instanceof PassiveAbility) && (ability.isToggleAbility() || !(((CoreBendingAbility) ability).isStopped()))) {
				return true;
			}
		}
		return false;
	}
	
	public List<BendingAbility> getAllCurrentPlayerAbilities(BendingPlayer bPlayer) {
		List<BendingAbility> abilities = new ArrayList<>();
		for (BendingAbility ability : ABILITY_INSTANCES) {
			if (ability.getPlayer().equals(bPlayer.getSpigotPlayer())) {
				abilities.add(ability);
			}
		}
		return abilities;
	}
	
	public BendingAbility getAbility(String name) {
		if (name == null) {
			return null;
		}
		if (!ABILITIES_BY_NAME.containsKey(name.toLowerCase())) {
			return null;
		}
		return ABILITIES_BY_NAME.get(name.toLowerCase());
	}
	
	
	public void registerAbility(BendingAbility ability) {
		if (!ABILITY_INSTANCES.contains(ability)) {
			ABILITY_INSTANCES.add(ability);
		}
	}
	
	public void unregisterAbility(BendingAbility ability) {
		if (ABILITY_INSTANCES.contains(ability)) {
			ABILITY_INSTANCES.remove(ability);
		}
	}
	
	@Override
	public void run() {
		for (int i=0; i<ABILITY_INSTANCES.size(); i++ ) {
			BendingAbility ability = ABILITY_INSTANCES.get(i);
			ability.update();
		}
	}
	
	
	public void registerAllPluginAbilities() {
		AbilityClassLoader abilityLoader = new AbilityClassLoader();
		final List<CoreBendingAbility> loadedAbilites = abilityLoader.loadAbilities();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "Ability Loader found " + loadedAbilites.size() + " abilities.");
		
		for (final CoreBendingAbility ability : loadedAbilites) {
			final String name = ability.getName();
			if (name == null) {
				continue;
			}
			try {
				ABILITIES_BY_NAME.put(name.toLowerCase(), ability);
				Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Loaded Ability: " + name);
			} catch(Exception | Error e) {
				e.printStackTrace();
				Bukkit.shutdown();
			}
		}
	}
	
	
}
