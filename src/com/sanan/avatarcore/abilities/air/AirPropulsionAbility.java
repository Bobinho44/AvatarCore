package com.sanan.avatarcore.abilities.air;

import org.bukkit.Location; 
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class AirPropulsionAbility extends AirBendingAbility {

	public AirPropulsionAbility(BendingPlayer player) {
		super(player);
		Vector thrust = getLocation().getDirection().normalize().add(new Vector(0, 1, 0)).multiply(1.25);
		this.player.getSpigotPlayer().getWorld().spawnParticle(Particle.CLOUD, getLocation(), 25, 0.5, 0.5, 0.5, 0.35);
		this.player.getSpigotPlayer().setVelocity(player.getSpigotPlayer().getVelocity().add(thrust));
		this.start();
	}

	@Override
	public void update() {
		super.update();
		remove();
	}
	
	@Override
	public String getName() {
		return "Air Propulsion";
	}

	@Override
	public String getDescription() {
		return "Blast the ground with air to propel yourself in the direction you are looking";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}
	
	@Override
	public String getDamageString() {
		return "";
	}
	
	@Override
	public Material getMaterial() {
		return Material.CLAY_BALL;
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
	public int getChiCost() {
		return 25;
	}

	@Override
	public int getLevelRequired() {
		return 10;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
