package com.sanan.avatarcore.abilities.water;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class MistSteppingAbility extends WaterBendingAbility implements EnduringAbility {
		
	long cooldown;
		
	public MistSteppingAbility(BendingPlayer player) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.start();
	}

	@Override
	public void update() {
		super.update();
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 2));
		getPlayer().getWorld().spawnParticle(Particle.WATER_BUBBLE, getLocation(), 5, 0.1, 0.1, 0.1, 0.1);
		if (System.currentTimeMillis() - cooldown >= 10000) {
			remove();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		getPlayer().removePotionEffect(PotionEffectType.SPEED);
	}
	
	@Override
	public String getName() {
		return "Mist Stepping";
	}

	@Override
	public String getDescription() {
		return "Speed booster.";
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
		return 25;
	}

	@Override
	public int getLevelRequired() {
		return 20;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
