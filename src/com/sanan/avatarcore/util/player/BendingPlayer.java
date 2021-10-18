package com.sanan.avatarcore.util.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sanan.avatarcore.util.player.nation.PlayerNationData;
import com.sanan.avatarcore.util.player.power.BendingPower;
import com.sanan.avatarcore.util.player.tutorial.BendingTutorial;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingItemUtil;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.player.chi.BendingChi;
import com.sanan.avatarcore.util.player.level.BendingLevel;

public class BendingPlayer {

	private Player spigotPlayer;
	
	private List<BendingTribe> joinRequests;
	
	private List<BendingElement> elements;
	private List<BendingHotbar> hotbars;
	
	private List<BendingAbility> passiveAbilityActivated;
	
	private ItemStack[] lastSavedInventory;

	private PlayerNationData playerNationData;
	private BendingPower playerPower;

	private long lastSneakStamp;
	private int shiftCounter;
	private boolean inBendingMode;
	private BendingHotbar equipped;
	
	private BendingChi chi;
	private int lastSavedExpLevel;
	private float lastSavedExpProgress;
	
	private BendingLevel level;
	
	private BendingTutorial tutorial;
	
	public BendingPlayer(Player spigotPlayer, List<BendingElement> elements, List<BendingHotbar> hotbars, int maximumChi, ArrayList<HashMap<String, Integer>> bendingInfoLevel, String bendingClass, List<BendingAbility> passiveAbilityActivated) {
		this(spigotPlayer);
		this.elements = elements;
		this.hotbars = hotbars;
		this.passiveAbilityActivated = passiveAbilityActivated;
		this.equipped = null;
		this.chi = new BendingChi(maximumChi);
		this.lastSavedExpLevel = spigotPlayer.getLevel();
		this.lastSavedExpProgress = spigotPlayer.getExp();
		this.level = new BendingLevel(bendingInfoLevel, bendingClass);
	}
	
	public BendingPlayer(Player spigotPlayer) {
		this.spigotPlayer = spigotPlayer;
		this.joinRequests = new ArrayList<>();
		this.elements = new ArrayList<>();
		this.hotbars = new ArrayList<>();
		this.passiveAbilityActivated = new ArrayList<>();
		this.lastSavedInventory = new ItemStack[36];
		this.lastSneakStamp = System.currentTimeMillis();
		this.playerNationData = PlayerNationData.createDefaultPlayerNationData();
		this.playerPower = new BendingPower();
		this.shiftCounter = 0;
		this.inBendingMode = false;
		this.chi = new BendingChi(100);
		this.lastSavedExpLevel = spigotPlayer.getLevel();
		this.lastSavedExpProgress = spigotPlayer.getExp();
		this.level = BendingLevel.createDefaultPlayerLevel();
	}
	
	public void startTutorial(double step) {
		this.tutorial = new BendingTutorial(spigotPlayer, step);
	}
	
	public void finishTutorial() {
		tutorial = null;
	}
	
	public boolean isSkipped() {
		return tutorial != null && tutorial.isSkipped();
	}
	
	public void setStep(double step) {
		tutorial.setStep(step);
	}
	
	public double getTutorialStep() {
		if (tutorial == null || tutorial.getStep() >= 9) {
			return -1;
		}
		return tutorial.getStep();
	}
	
	public BendingTutorial getTutorial() {
		return tutorial;
	}
	
	public void nextStepTutorial() {
		tutorial.nextStep();
	}
	
	public void previoustepTutorial() {
		tutorial.previousStep();
	}
	
	public void skipTutorial() {
		tutorial.skip();
	}
	
	public boolean hasFinishTutorial() {
		return tutorial == null;
	}
	
	public BendingLevel getBendingLevel() {
		return level;
	}
	
	public void addXp(int xp) {
		getBendingLevel().addXp(xp, spigotPlayer);
	}
	
	public int getLevel(String bendingClass) {
		return getBendingLevel().getLevel(bendingClass);
	}
	
	public List<BendingAbility> getPassiveAbilityActivated() {
		return passiveAbilityActivated;
	}
	public void activatePassiveAbility(BendingAbility passiveAbility) {
		passiveAbilityActivated.add(passiveAbility);
	}
	
	public void desactivatePassiveAbility(BendingAbility passiveAbility) {
		passiveAbilityActivated.remove(passiveAbility);
	}
	
	public BendingChi getChi() {
		return chi;
	}
	public BendingHotbar getEquippedHotbar() {
		return equipped;
	}
	public boolean isInBendingMode() {
		return inBendingMode;
	}
	public List<BendingHotbar> getHotbars() {
		return hotbars;
	}
	public int getShiftCounter() {
		return shiftCounter;
	}
	public long getLastSneakStamp() {
		return lastSneakStamp;
	}
	public ItemStack[] getLastSavedInventory() {
		return lastSavedInventory;
	}
	public void setShiftCounter(int shiftCounter) {
		this.shiftCounter = shiftCounter;
	}
	public void setLastSavedInventory(ItemStack[] lastSavedInventory) {
		this.lastSavedInventory = lastSavedInventory;
	}
	public void updateLastSneakStamp() {
		this.lastSneakStamp = System.currentTimeMillis();
	}
	public List<BendingElement> getElements() {
		return elements;
	}

	public PlayerNationData getPlayerNationData() {
		return this.playerNationData;
	}

	public BendingPower getPlayerPower() {
		return this.playerPower;
	}

	public void setPlayerPower(int power) {
		this.playerPower = this.playerPower.set(power);
	}

	public void increasePlayerPower() {
		this.playerPower = this.playerPower.increaseBy(1);
	}

	public void increasePlayerPowerBy(int amount) {
		this.playerPower = this.playerPower.increaseBy(amount);
	}

	public void decreasePlayerPowerBy(int amount) {
		this.playerPower = this.playerPower.decreaseBy(amount);
	}

	public void updateNationData(String nation) {
		this.playerNationData = this.playerNationData.updateNation(nation, this.playerNationData);
	}

	public void updateNationData() {
		this.playerNationData = this.playerNationData.updateBasingOn(this.playerNationData);
	}

	protected void setPlayerNationData(PlayerNationData newPlayerNationData) {
		this.playerNationData = newPlayerNationData;
	}

	public Player getSpigotPlayer() {
		return spigotPlayer;
	}
	
	public void setEquippedHotbar(BendingHotbar equipped) {
		this.equipped = equipped;
	}
	
	
	/*
	 * Bending Mode
	 */
	public boolean isRenamingHotbar() {
		for (BendingHotbar hotbar : this.hotbars)
			if (hotbar.isRenaming()) return true;
		return false;
	}
	
	public BendingHotbar getRenamingHotbar() {
		for (BendingHotbar hotbar : this.hotbars)
			if (hotbar.isRenaming()) return hotbar;
		return null;
	}
	
	public void addBendingHotbar(BendingHotbar hotbar) {
		if (!this.hotbars.contains(hotbar)) this.hotbars.add(hotbar);
	}
	public void removeBendingHotbar(BendingHotbar hotbar) {
		if (this.hotbars.contains(hotbar)) this.hotbars.remove(hotbar);
	}
	public void clearBendingHotbars() {
		hotbars.clear();
	}
	
	public void toggleBendingMode() {
		if (isInBendingMode()) {
			//Return to normal mode
			for (int i=0; i<36; i++) {
				this.spigotPlayer.getInventory().setItem(i, this.lastSavedInventory[i]);
			}
			//Stop renaming hotbars
			for (BendingHotbar hotbar : this.hotbars) {
				if (hotbar.isRenaming()) hotbar.setRenaming(false);
			}
			this.spigotPlayer.setLevel(this.lastSavedExpLevel);
			this.spigotPlayer.setExp(this.lastSavedExpProgress);
			this.inBendingMode = false;
		}
		else {
			//Enter bending mode
			this.lastSavedExpLevel = this.spigotPlayer.getLevel();
			this.lastSavedExpProgress = this.spigotPlayer.getExp();
			savePlayerInventory();
			applyBendingInventory();
			applyBendingHotbarAbilities();
			updateBendingChiBar();
			
			this.inBendingMode = true;
		}
	}
	
	public boolean canAffordBendingAbility(BendingAbility ability) {
		return (this.chi.getCurrent() >= ability.getChiCost());
	}
	
	
	public void savePlayerInventory() {
		for (int i=0; i<36; i++) {
			this.lastSavedInventory[i] = this.spigotPlayer.getInventory().getItem(i);
		}
	}
	public void updateBendingChiBar() {
		this.spigotPlayer.setLevel(this.chi.getCurrent());
		this.spigotPlayer.setExp((float) this.chi.getCurrent() / this.chi.getMaximum());
	}
	
	public boolean isEquipped() {
		boolean isEquipped = false;
		if (this.equipped != null) {
			for (int i=0; i<9; i++) {
				if (this.equipped.getAbilities()[i] != null) {
					isEquipped = true;
					break;
				}
			}
		}
		return isEquipped;
	}
	
	public void applyBendingInventory() {
		Player player = this.spigotPlayer;
		boolean isEquipped = isEquipped();
		
		for (int i=0; i<36; i++) {
			player.getInventory().setItem(i, null);
		}
		player.getInventory().setItem(isEquipped ? 16 : 7, BendingItemUtil.getManageHotbarsAccessor());
		player.getInventory().setItem(isEquipped ? 17 : 8, BendingItemUtil.getManageHotbarsPassiveAbility());
		
		int curr = isEquipped ? 9 : 0;
		for (BendingHotbar hotbar : this.hotbars) {
			ItemStack item = BendingItemUtil.getHotbarDisplayItem(hotbar);
			if (hotbar.equals(equipped)) {
				item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
				item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			player.getInventory().setItem(curr, item);
			curr++;
		}
		player.updateInventory();
	}
	
	public void applyBendingHotbarAbilities() {
		Player player = this.spigotPlayer;
		boolean isEquipped = isEquipped();
		
		if (isEquipped) {
			player.getInventory().setItem(7, BendingItemUtil.getManageHotbarsAccessor());
			player.getInventory().setItem(8, BendingItemUtil.getManageHotbarsPassiveAbility());
			
			int curr = 0;
			for (BendingHotbar hotbar : this.hotbars) {
				ItemStack item = BendingItemUtil.getHotbarDisplayItem(hotbar);
				if (hotbar.equals(equipped)) {
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				player.getInventory().setItem(curr, item);
				curr++;
			}
			
			for (int i=0; i<9; i++) {
				if (this.equipped.getAbilities()[i] != null)
					player.getInventory().setItem(i, this.equipped.getAbilities()[i].getItem());
				else
					player.getInventory().setItem(i, null);
			}
		}
	}
		
	/*
	 * Invite Request
	 */
	public void acceptJoinRequest(BendingTribe tribe) {
		if (this.hasJoinRequest(tribe)) {
			this.joinRequests.remove(tribe);
			tribe.addMember(getSpigotPlayer().getUniqueId());
		}
	}
	
	public void declineJoinRequest(BendingTribe tribe) {
		if (this.hasJoinRequest(tribe)) {
			this.joinRequests.remove(tribe);
		}
	}
	
	public void createJoinRequest(BendingTribe tribe) {
		this.joinRequests.add(tribe);
	}
	
	public boolean hasJoinRequest(BendingTribe tribe) {
		return getJoinRequests().contains(tribe);
	}
	
	public List<BendingTribe> getJoinRequests() {
		return joinRequests;
	}
	
	public void sendJoinRequest(BendingTribe tribe) {
		if (tribe.getMemberNumber() < tribe.getMaxPlayer() && !tribe.hasJoinRequest(this)) {
			tribe.createJoinRequest(this);
    		new BukkitRunnable() {
				public void run() {
					removeJoinRequest(tribe);
				}
			}.runTaskLaterAsynchronously(AvatarCore.getInstance(), 12000);
		}
	}
	
	public void removeJoinRequest(BendingTribe tribe) {
		if (tribe.hasJoinRequest(this)) {
			tribe.declineJoinRequest(this);
		}
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof BendingPlayer)) return false;
		
		BendingPlayer other = (BendingPlayer)obj;
		
		return other.getSpigotPlayer().getUniqueId().toString().equalsIgnoreCase(this.getSpigotPlayer().getUniqueId().toString());
	}
	
}
