package com.sanan.avatarcore.abilities.earth.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class HeartyAbility extends EarthBendingAbility implements PassiveAbility {

	public HeartyAbility(BendingPlayer player) {
		super(player);
		this.start();
	}
	
	@Override
	public void update() {
		super.update();
		getPlayer().setHealthScale(22);
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().setHealthScale(20);
	}
	
	@Override
	public String getName() {
		return "Hearty";
	}

	@Override
	public String getDescription() {
		return "Become stronger thanks to an extra heart.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.HEART_OF_THE_SEA;
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
