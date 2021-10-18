package com.sanan.avatarcore.util.crate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.component.ComponentUtil;
import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.item.ItemBuilder;

public class BendingCrateManager {

	private ConfigManager cm = ConfigManager.getInstance();
	
	private static BendingCrateManager instance;
	
	private CrateCollection heroCollection = new CrateCollection();
	private CrateCollection championCollection = new CrateCollection();
	private CrateCollection masterCollection = new CrateCollection();
	private CrateCollection lordCollection = new CrateCollection();
	private CrateCollection legendCollection = new CrateCollection();
	private CrateCollection whiteLotusCollection = new CrateCollection();
	
	public static BendingCrateManager getInstance() {
		if (instance == null) {
			instance = new BendingCrateManager();
		}
		return instance;
	}
	
	public Map<ItemStack, Double> getItemsCollection(String type) {
		return getGoodCollection(type).getCollection();
	}
	
	public ItemStack getRandomItem(int round, String type) {
		return getGoodCollection(type).next(round);
	}
	
	public void setNewCollection(Map<ItemStack, Double> newCollection, String type) {
		getGoodCollection(type).setNewCollection(newCollection);
	}
	
	public double getPercent(ItemStack item) {
		if (!isInformationItem(item)) {
			return 50;
		}
		List<String> lore = new ArrayList<String>(item.getItemMeta().getLore());
		return Double.valueOf(ChatColor.stripColor(lore.get(lore.size() - 1)).replace("Percent: ", ""));
	}
	
	public ItemStack getInformationItem(ItemStack item, double percent) {
		percent = Math.round(percent * 10.0) / 10.0;
		if (isInformationItem(item)) {
			return getInformationItem(getNormalItem(item), percent);
		}
		return new ItemStack(new ItemBuilder(item.clone()).addLore("§aPercent: " + percent).build());
	}
	
	public ItemStack getNormalItem(ItemStack item) {
		if (!isInformationItem(item)) {
			return item.clone();
		}
		List<String> lore = new ArrayList<String>(item.getItemMeta().getLore());
		lore.remove(lore.size() - 1);
		return new ItemStack(new ItemBuilder(item.clone()).setLore(lore.toArray(new String[0])).build());
	}
	
	private boolean isInformationItem(ItemStack item) {
		return item.getItemMeta().hasLore() && ChatColor.stripColor(item.getItemMeta().getLore().toString()).contains("Percent: ");
	}
	
	public CrateCollection getGoodCollection(String type) {
		switch (type.toLowerCase()) {
		case "whitelotus":
			return whiteLotusCollection;
		case "legend":
			return legendCollection;
		case "lord":
			return lordCollection;
		case "master":
			return masterCollection;
		case "champion":
			return championCollection;
		default:
			return heroCollection;
		}
	}
	
	public void saveAllCrateCollections() {
		cm.clearCrateData();
		List<CrateCollection> collections = new ArrayList<CrateCollection>(Arrays.asList(heroCollection, championCollection, masterCollection, lordCollection, legendCollection, whiteLotusCollection));
		List<Map<ItemStack, Double>> maps = new ArrayList<Map<ItemStack, Double>>();
		for (int i = 0; i < 6; i++) {
			maps.add(collections.get(i).getCollection());
		}
		cm.getCrateData().set("collections", maps);
		cm.saveCrateData();
	}
	
	@SuppressWarnings("unchecked")
	public void registerAllCrateCollections() {
		List<CrateCollection> collections = new ArrayList<CrateCollection>(Arrays.asList(heroCollection, championCollection, masterCollection, lordCollection, legendCollection, whiteLotusCollection));
		List<Map<?, ?>> maps = cm.getCrateData().getMapList("collections");
		if (maps.size() > 0) {
			for (int i = 0; i < 6; i++) {
				collections.get(i).setNewCollection((Map<ItemStack, Double>) maps.get(i));
			}
		}
	}
}
