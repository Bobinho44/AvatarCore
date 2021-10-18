package com.sanan.avatarcore.abilities.water.passive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class DepthStriderAbility extends WaterBendingAbility implements PassiveAbility {

	public DepthStriderAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 99999999, 1));
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
	}
	
	@Override
	public String getName() {
		return "Depth Strider";
	}

	@Override
	public String getDescription() {
		return "Grants the bender depth strider III.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_BOOTS;
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
		return 25;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}

