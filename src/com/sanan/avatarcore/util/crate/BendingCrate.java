package com.sanan.avatarcore.util.crate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public class BendingCrate {

	private final AvatarCore ac = AvatarCore.getInstance();
	private final BendingCrateManager bcm = BendingCrateManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	
	private List<ItemStack> firstRound;
	private List<ItemStack> secondRound;
	private List<ItemStack> thirdRound;
	private int step = 0;
	private Player player;
	private Inventory menu;
	private String type;
	private int start = 0;
	
	public BendingCrate(Player player, String type) {
		this.player = player;
		this.type = type;
		this.firstRound = new ArrayList<ItemStack>(bcm.getItemsCollection(type).keySet());
		this.secondRound = new ArrayList<ItemStack>(bcm.getItemsCollection(type).keySet());
		this.thirdRound = new ArrayList<ItemStack>(bcm.getItemsCollection(type).keySet());
		Collections.shuffle(firstRound);
		Collections.shuffle(secondRound);
		Collections.shuffle(thirdRound);
		this.menu = CrateInventoryUtil.getCratePlayMenu(this, type);
		player.openInventory(menu);
	}
	
	public List<ItemStack> getItems(int roundNumber) {
		switch (roundNumber) {
		case 3:
			return getSubList(thirdRound);
		case 2:
			return getSubList(secondRound);
		default:
			return getSubList(firstRound);
		}
	}
	
	private List<ItemStack> getSubList(List<ItemStack> collection) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = step; i < step + 5; i++) {
			items.add(collection.get(i % collection.size()));
		}
		return items;
	}
	
	public boolean isStarted() {
		return start == 1;
	}
	
	public void stop() {
		start = 0;
	}
	
	public void animation() {
		step = 0;
		start = 1;
		menu.setItem(26, CrateItemUtil.getCrateMenuDivider(Material.BLACK_STAINED_GLASS_PANE));
		new BukkitRunnable() {
			int isFinish;
			List<ItemStack> rewards = new ArrayList<ItemStack>();
			int[] greenDividerIndex = {19, 21, 23, 25};
		    public void run() {
		    	if (start != 1) {
		    		cancel();
		    	}
		    	else {
			    	for (int greenDividerIndex : greenDividerIndex) {
			    		menu.setItem(greenDividerIndex, CrateItemUtil.getGlowingDivider(step));
			    	}
			    	step++;
			    	if (step == 40) {
				    	for (int j = 0; j < isFinish; j++) {
				    		rewards.add(bcm.getRandomItem(j+1, type));
				    	}
			    	}
			    	isFinish = 0;
			    	for (int i = 0; i < 3; i++) {
			    		if (menu.getItem(47 + 2 * i) != null) {
			    			CrateInventoryUtil.setRound(BendingCrate.this, menu, i+1);
			    			if (step >= 40) {
				    			if (menu.getItem(20 + 2 * i).equals(rewards.get(i))) {
				    				menu.setItem(47 + 2 * i, null);
				    				isFinish--;
				    			}
			    			}
			    			isFinish++;
			    		}
					}
			    	if (isFinish <= 0 && step % 2 == 1) {
			    		cancel();
			    		pm.give(player, rewards);
			    		start = 0;
			    		for (ItemStack reward : rewards) {
			    			player.sendMessage("You win : " + reward.getType());
			    		}
			    		BendingPlayer bPlayer = pm.getBendingPlayer(player);
						if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() == 7.0) {
							player.closeInventory();
							bPlayer.nextStepTutorial();
						}
			    	}
		    	}
		    }
		}.runTaskTimer(ac, 0, 2);
	}
	
}
