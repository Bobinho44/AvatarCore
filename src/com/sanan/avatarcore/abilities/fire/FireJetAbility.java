package com.sanan.avatarcore.abilities.fire;

import org.bukkit.Location; 
import org.bukkit.Material;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class FireJetAbility extends FireBendingAbility {
	
	private float speed;
	
	public FireJetAbility(BendingPlayer player, float speed) {
		super(player);
		this.speed = speed;
		this.player.getSpigotPlayer().setAllowFlight(true);
		this.player.getSpigotPlayer().setFlying(true);
		this.player.getSpigotPlayer().setFlySpeed(0.2f);
		this.start();
	}
	
	@Override
	public void update() {
		super.update();
		Location jetCenter = getLocation().subtract(0,0.2,0);
		sendFireParticle(jetCenter, 75, 0.07, -0.07, 0.07, 0.075);
		getPlayer().setVelocity(getPlayer().getVelocity().add(getLocation().getDirection().normalize().multiply(speed).add(new Vector(0, 0.075, 0))));
	}
	
	@Override
	public void remove() {
		super.remove();
		player.getSpigotPlayer().setFlySpeed(0.2f);
		player.getSpigotPlayer().setAllowFlight(false);
		player.getSpigotPlayer().setFlying(false);
	}
	
	@Override
	public String getName() {
		return "Fire Jet";
	}

	@Override
	public String getDescription() {
		return "Create a Jet of flames under your feet, propelling you in the direction you look";
	}

	@Override
	public Location getLocation() {
		return this.getPlayer().getLocation();
	}

	@Override
	public String getDamageString() {
		return "";
	}
	
	@Override
	public Material getMaterial() {
		return Material.CAMPFIRE;
	}
	
	public float getSpeed() {
		return speed;
	}

	@Override
	public boolean isToggleAbility() {
		return true;
	}

	@Override
	public boolean isDamageAbility() {
		return false;
	}
	
	@Override
	public int getChiCost() {
		return 15;
	}
	
	@Override
	public int getLevelRequired() {
		return 10;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 100;
	}
	
}
