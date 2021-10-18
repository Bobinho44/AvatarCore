package com.sanan.avatarcore.abilities.fire.passive;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class BlueFireAbility extends FireBendingAbility implements PassiveAbility {

	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	public BlueFireAbility(BendingPlayer player) {
		super(player);
		badm.showBlueClue();
		this.start();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void remove() {
		super.remove();
		badm.hideBlueClue(getPlayer());
	}
	
	@Override
	public String getName() {
		return "Blue Fire";
	}

	@Override
	public String getDescription() {
		return "Adds an additional .75 damage to every offensive attack.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.BLUE_DYE;
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
		return 60;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}

