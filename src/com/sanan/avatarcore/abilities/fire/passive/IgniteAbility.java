package com.sanan.avatarcore.abilities.fire.passive;

import org.bukkit.Location; 
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class IgniteAbility extends FireBendingAbility implements PassiveAbility {
	
	public IgniteAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public String getName() {
		return "Ignite";
	}

	@Override
	public String getDescription() {
		return "Set fire to everything .";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}
	
	@Override
	public String getDamageString() {
		return "";
	}
	
	@Override
	public Material getMaterial() {
		return Material.FLINT_AND_STEEL;
	}
	
	@Override
	public boolean isToggleAbility() {
		return false;
	}

	@Override
	public boolean isDamageAbility() {
		return false;
	}
	
	@Override
	public int getChiCost() {
		return 0;
	}

	@Override
	public int getLevelRequired() {
		return 30;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}