package com.sanan.avatarcore.util.player.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.crate.CrateItemUtil;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public class BendingLevel {

	private final PlayerManager pm = PlayerManager.getInstance();
	private final BendingAbilityManager bam = BendingAbilityManager.getInstance();
	
	private HashMap<String, Integer> bendingXp;
	private HashMap<String, Integer> bendingLevel;
	private String bendingClass;
	
	public static enum LevelClass {
		  EARTH,
		  FIRE,
		  AIR,
		  WATER
	}
	
	public BendingLevel(ArrayList<HashMap<String, Integer>> bendingInfoLevel, String bendingClass) {
		this.bendingXp = bendingInfoLevel.get(0);
		this.bendingLevel = bendingInfoLevel.get(1);
		this.bendingClass = bendingClass;
	}
	
	public HashMap<String, Integer> getBendingXp() {
		return bendingXp;
	}
	
	public HashMap<String, Integer> getBendingLevel() {
		return bendingLevel;
	}
	
	 public int getLevel(String...bendingClass) {
		 String bClass = (bendingClass.length > 0 && bendingClass[0] != null) ? bendingClass[0] : this.bendingClass;
		 if (bClass.equalsIgnoreCase("none")) return 0;
		 if (bClass.equalsIgnoreCase("avatar")) return 101;
		 return bendingLevel.get(bClass);
		}
	
	public int getXp() {
		return bendingXp.get(bendingClass);
	}
	
	public String getBendingClass() {
		return bendingClass;
	}
	
	public String setClass(String bendingClass) {
		if (getBendingClass().equals(bendingClass)) return "You already use this class!"; 
		if (getLevel(this.bendingClass) % 100 != 0) return "You must max out your current bending class before you can choose an additional class!"; 
		if (getLevel(bendingClass) == 100) return "You already max this class!"; 
		this.bendingClass = bendingClass;
		bendingLevel.put(bendingClass, 1);
		return getBendingInfo();
	}
	
	public int getNextLevelXp() {
		int level = getLevel();
		Preconditions.checkArgument(level > 0, "The player has not activated this class.");
		return (int) (100 * ( (1 - Math.pow(1.1, (level == 100 ? 99 : level))) / -0.1 ));
	}
	
	public static BendingLevel createDefaultPlayerLevel() {
		final ArrayList<HashMap<String, Integer>> bendingLevelInfo = new ArrayList<HashMap<String, Integer>>(Arrays.asList(new HashMap<String, Integer>(),new HashMap<String, Integer>()));
		for (LevelClass element : LevelClass.values()) {
			bendingLevelInfo.get(0).put(element.toString().toLowerCase(), 0);
			bendingLevelInfo.get(1).put(element.toString().toLowerCase(), 0);
		}
		return new BendingLevel(bendingLevelInfo, "none");
	}
	
	public void addXp(int xp, Player player) {
		if (bendingClass.equals("none") || getLevel() >= 100) return;
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		player.sendMessage(ChatColor.GREEN + "+" + xp +" bendingXP");
		if (getLevel() != 100) 
			bendingXp.put(bendingClass, bendingXp.get(bendingClass) + xp); 
		int nbLevel = 0;
		while (getXp() >= getNextLevelXp() && getLevel() < 100) {
			bendingLevel.put(bendingClass, bendingLevel.get(bendingClass) + 1);
			giveShard(player, getLevel());
			nbLevel++;
		}
		if (getLevel() == 100) {
			player.sendMessage("§6You have mastered this bending path! You can now walk through the §b§oUpgrade Your Bending §6portal at spawn and choose an additional bending path!");
			bPlayer.getChi().setMaximum(bPlayer.getChi().getMaximum() + 50);
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set essentials.warps.upgrade true");
		}
		else if (nbLevel != 0) { 
			player.sendMessage(BendingElement.getBendingElement(bendingClass).getColor() + "LevelUp! +" + nbLevel + "\n" + getBendingInfo()); 
		}
		
		//earth levitation +
		if (getBendingClass().equalsIgnoreCase("earth") && getLevel() >= 55) {
			for (BendingHotbar hotbar : bPlayer.getHotbars()) {				
				for (int i=0; i<9; i++) {
					if (hotbar.getAbilities()[i] != null) {
						if (hotbar.getAbilities()[i].getName().equalsIgnoreCase("earth levitation")) {
							hotbar.getAbilities()[i] = bam.getAbility("Earth Levitation +");
						}
					}
				}
			}
			if (bPlayer.isInBendingMode()) {
				bPlayer.applyBendingInventory();
				bPlayer.applyBendingHotbarAbilities();
			}
		}
		
		for (Integer level : bendingLevel.values()) {
			if (level != 100) { return; }
		}
		bendingClass = "avatar";
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set essentials.warps.upgrade false");
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " parent add avatar");
		Bukkit.getOnlinePlayers().forEach(serverPlayer -> serverPlayer.sendMessage(ChatColor.GOLD +
				 	"Congratulation " + player.getName()+ " has masted every bending ability and is now an Avatar!"));
	}
	
	public String getBendingInfo(String... bendingClass) {
		if (bendingClass.length > 0 && bendingClass[0] != null) {
			for (BendingElement element : BendingElement.getBendingElements()) {
				if (element.getName().toLowerCase().contains((bendingClass[0]))) {
					String bendingClassName = bendingClass[0].substring(0, 1).toUpperCase() + bendingClass[0].substring(1);
					if (getLevel(bendingClass[0]) == 100) {
						return element.getColor() + bendingClassName + ": Mastered";
					}
					return element.getColor() + bendingClassName + ": Level: " + getLevel() + " - Next Level: " + getXp() + "/" + getNextLevelXp() + " xp";
				}
			}
			return "You do not have a class selected.";
		}
		if (this.bendingClass.equalsIgnoreCase("Avatar")) {
			return ChatColor.GOLD +	"You are an Avatar!";
		}
		return getBendingInfo(getBendingClass()); 
	}
	
	private void giveShard(Player player, int level) {
		if (pm.getBendingPlayer(player).hasFinishTutorial()) {
			ItemStack shard = null;
			int amount = 0;
			if (level <= 20) {
				shard = CrateItemUtil.getCrateShard("hero");
				amount = level > 10 ? 10 : level - 1;
			}
			else if (level <= 40) {
				shard = CrateItemUtil.getCrateShard("champion");
				amount = level > 29 ? 10 : level - 20;
			}
			else if (level <= 60) {
				shard = CrateItemUtil.getCrateShard("master");
				amount = level > 49 ? 10 : level - 40;
			}
			else if (level <= 80) {
				shard = CrateItemUtil.getCrateShard("lord");
				amount = level > 69 ? 10 : level - 60;
			}
			else if (level <= 100) {
				shard = CrateItemUtil.getCrateShard("legend");
				amount = level > 89 ? 10 : level - 80;
			}
			shard.setAmount(amount);
			Bukkit.getConsoleSender().sendMessage(shard.toString());
			pm.give(player, Arrays.asList(shard));
		}
	}
	
}
