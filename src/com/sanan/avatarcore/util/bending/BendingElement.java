package com.sanan.avatarcore.util.bending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.bending.ability.hotbar.BendingItemUtil;

import net.md_5.bungee.api.ChatColor;

public class BendingElement {
	
	private static final HashMap<String, BendingElement> BENDING_ELEMENTS = new HashMap<>();
	
	private final String name;
	private final ChatColor color;
	private final ItemStack item;
	
	/*
	 * ELEMENTS
	 */
	public static final BendingElement AIR = new BendingElement("Air", ChatColor.GRAY, BendingItemUtil.getAirElementItem());
	public static final BendingElement WATER = new BendingElement("Water", ChatColor.AQUA, BendingItemUtil.getWaterElementItem());
	public static final BendingElement EARTH = new BendingElement("Earth", ChatColor.GREEN, BendingItemUtil.getEarthElementItem());
	public static final BendingElement FIRE = new BendingElement("Fire", ChatColor.RED, BendingItemUtil.getFireElementItem());
	
	public BendingElement(final String name, final ChatColor color, final ItemStack item) {
		this.name = name;
		this.color = color;
		this.item = item;
		BENDING_ELEMENTS.put(name.toLowerCase(), this);
	}
	
	public String getName() {
		return this.getColor() + this.name;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public static BendingElement getBendingElement(final String name) {
		if (name == null) return null;
		return BENDING_ELEMENTS.get(name.toLowerCase());
	}
	
	public static List<BendingElement> getBendingElements() {
		List<BendingElement> newList = new ArrayList<>();
		newList.addAll(BENDING_ELEMENTS.values());
		return newList;
	}
	
	public boolean isBendingOppositeElement(BendingElement element) {
		return (this.equals(AIR) && element.equals(EARTH)
			 || this.equals(EARTH) && element.equals(AIR)
			 || this.equals(FIRE) && element.equals(WATER)
			 || this.equals(WATER) && element.equals(FIRE));
		
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BendingElement)) return false;
		BendingElement tested = (BendingElement) obj;
		return getName().equals(tested.getName());
	}

	public static BendingElement fromString(final String element) {
		if (element == null || element.equals("")) return null;
		if (getBendingElement(element) != null) return getBendingElement(element);
		return null;
	}
	
}
