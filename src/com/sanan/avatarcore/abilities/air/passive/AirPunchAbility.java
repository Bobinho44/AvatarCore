package com.sanan.avatarcore.abilities.air.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class AirPunchAbility extends AirBendingAbility implements PassiveAbility {

	public AirPunchAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public String getName() {
		return "Air Punch";
	}

	@Override
	public String getDescription() {
		return "Causes any unarmed attacks to deal knockback 4.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.CROSSBOW;
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
	public String getDamageString() {
		return "";
	}

	@Override
	public int getChiCost() {
		return 0;
	}

	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}