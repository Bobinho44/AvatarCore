package com.sanan.avatarcore.abilities.earth;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthLevitationPlusAbility extends EarthBendingAbility {
	
	public EarthLevitationPlusAbility(BendingPlayer player) {
		super(player);
	}

	@Override
	public void update() {
		super.update();
		remove();
	}
	
	@Override
	public String getName() {
		return "Earth Levitation +";
	}

	@Override
	public String getDescription() {
		return "Extract and take control of a chunk of earth, which you can throw at your enemies.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.GRASS_BLOCK;
	}

	@Override
	public boolean isToggleAbility() {
		return false;
	}

	@Override
	public boolean isDamageAbility() {
		return true;
	}

	@Override
	public String getDamageString() {
		return "6";
	}

	@Override
	public int getChiCost() {
		return 35;
	}
	
	@Override
	public int getLevelRequired() {
		return 55;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
