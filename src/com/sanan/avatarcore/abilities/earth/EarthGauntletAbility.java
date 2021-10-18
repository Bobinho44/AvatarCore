package com.sanan.avatarcore.abilities.earth;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthGauntletAbility extends EarthBendingAbility implements EnduringAbility {
	
	private long cooldown;
	
	public EarthGauntletAbility(BendingPlayer player) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 2));
		this.start();
	}

	@Override
	public void update() {
		super.update();
		if (System.currentTimeMillis() - cooldown >= 30000) {
			remove();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
	}
	
	@Override
	public String getName() {
		return "Earth Gauntlet";
	}

	@Override
	public String getDescription() {
		return "Get a physical boost";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.GOLDEN_SWORD;
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
		return 40;
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
