package com.sanan.avatarcore.abilities.earth.passive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class CaveSightAbility extends EarthBendingAbility implements PassiveAbility{

	public CaveSightAbility(BendingPlayer player) {
		super(player);
		this.start();
	}
	
	@Override
	public void update() {
		super.update();
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999999, 1));
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
	}
	
	@Override
	public String getName() {
		return "Cave Sight";
	}

	@Override
	public String getDescription() {
		return "Seeing in the dark.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.POTION;
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
