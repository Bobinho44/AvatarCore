package com.sanan.avatarcore.abilities.air.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class AirCushionAbility extends AirBendingAbility implements PassiveAbility {

	public AirCushionAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public String getName() {
		return "Air Cushion";
	}

	@Override
	public String getDescription() {
		return "Prevents the air bender from taking fall damage.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.FEATHER;
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
		return 1;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}

