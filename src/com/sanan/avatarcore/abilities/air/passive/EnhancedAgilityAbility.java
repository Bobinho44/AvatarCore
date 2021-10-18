package com.sanan.avatarcore.abilities.air.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EnhancedAgilityAbility extends AirBendingAbility implements PassiveAbility {

	public EnhancedAgilityAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().setAllowFlight(false);
        getPlayer().setFlying(false);
	}
	
	@Override
	public String getName() {
		return "Enhanced Agility";
	}

	@Override
	public String getDescription() {
		return "Allows the air bender to double jump.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.SLIME_BLOCK;
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
		return 30;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}

}
