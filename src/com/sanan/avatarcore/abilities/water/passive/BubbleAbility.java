package com.sanan.avatarcore.abilities.water.passive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class BubbleAbility extends WaterBendingAbility implements PassiveAbility {

	public BubbleAbility(BendingPlayer player) {
		super(player);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 99999999, 0));
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.WATER_BREATHING);
	}
	
	@Override
	public String getName() {
		return "Bubble";
	}

	@Override
	public String getDescription() {
		return "Create a bubble of air around the benders head.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.TURTLE_HELMET;
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

