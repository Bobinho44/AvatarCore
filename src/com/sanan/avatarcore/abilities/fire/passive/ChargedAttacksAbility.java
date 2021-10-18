package com.sanan.avatarcore.abilities.fire.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class ChargedAttacksAbility extends FireBendingAbility implements PassiveAbility {

	public ChargedAttacksAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public String getName() {
		return "Charged Attacks";
	}

	@Override
	public String getDescription() {
		return "Allows a player to charge up the abilities.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.TNT;
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
		return 90;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}

