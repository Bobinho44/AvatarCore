package com.sanan.avatarcore.abilities.air;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class SuctionAbility extends AirBendingAbility {

	private Random r;
	private List<Location> particles = new ArrayList<>();
	private double dist;
	private double step = 0.5;
	
	public SuctionAbility(BendingPlayer player) {
		super(player);
		this.r = new Random();
		this.dist = 0;
		newParticles();
		this.start();
	}

	@Override
	public void update() {
		super.update();
		for (Location loc : particles) {
			Vector dir = getPlayer().getLocation().toVector().subtract(loc.toVector()).normalize();
			Location spawn = loc.add(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist);
			spawn.getWorld().spawnParticle(Particle.CLOUD, spawn, 1, 0, 0, 0, 0);
		}
		dist += step;
		if (dist >= 3) {
			dist = 0;
			newParticles();
		}
		pullEntities(0.25);
	}
	
	
	private void pullEntities(double mag) {
		for (Entity ent : getPlayer().getNearbyEntities(10, 10, 10)) {
			Vector dir = getPlayer().getLocation().toVector().subtract(ent.getLocation().toVector()).normalize();
			if (ent instanceof LivingEntity) {
				ent.setVelocity(ent.getVelocity().add(dir.multiply(mag)));
			}
		}
	}
	
	private void newParticles() {
		particles.clear();
		for (int i=0; i<25; i++) {
			int x = -10 +  r.nextInt(20);
			int y = r.nextInt(20);
			int z = -10 + r.nextInt(20);
			Location partLoc = getPlayer().getLocation().add(x, y+1, z);
			particles.add(partLoc);
		}
	}
	
	@Override
	public String getName() {
		return "Suction";
	}

	@Override
	public String getDescription() {
		return "Vaccum air towards yourself pulling everything towards you";
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
		return Material.MILK_BUCKET;
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
		return 10;
	}
	
	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 100;
	}
	
}
