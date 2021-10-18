package com.sanan.avatarcore.util.bending.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.bending.BendingElement;

public interface BendingAbility {
	
	public void start();
	
	public void remove();
	
	public void update();
	
	public Player getPlayer();
	
	public String getName();
	
	public String getDescription();
	
	public BendingElement getElement();
	
	public Location getLocation();
	
	public Material getMaterial();
	
	public ItemStack getItem();
	
	public boolean isToggleAbility();
	
	public boolean isDamageAbility();
	
	public String getDamageString();
	
	public int getChiCost();
	
	public int getLevelRequired();
	
	public int getToogleChiConsumptionTime();
}
