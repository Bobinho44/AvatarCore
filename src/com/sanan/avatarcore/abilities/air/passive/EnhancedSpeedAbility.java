package com.sanan.avatarcore.abilities.air.passive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EnhancedSpeedAbility extends AirBendingAbility implements PassiveAbility  {

	public EnhancedSpeedAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.SPEED);
	}
	
	@Override
	public String getName() {
		return "Enhanced Speed";
	}

	@Override
	public String getDescription() {
		return "gives the air bender speed 2.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.IRON_BOOTS;
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
		return 15;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}

}

