package com.sanan.avatarcore.abilities.air;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.ability.air.AirBendingAbility;
import com.sanan.avatarcore.util.collision.CollisionUtil;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class AirswipeAbility extends AirBendingAbility {
	
	private Location start;
	private Location curr;
	private long cooldown;
	private double step;
	private double dist;
	
	public AirswipeAbility(BendingPlayer player, double step) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.step = step;
		this.start = player.getSpigotPlayer().getLocation().add(0, 1, 0);
		Vector dir = player.getSpigotPlayer().getLocation().getDirection().normalize();
		this.curr = start.add(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist);
		this.dist += step;
		this.start();
	}

	@Override
	public void update() {
		super.update();
		stop();
		Vector dir = player.getSpigotPlayer().getLocation().getDirection().normalize();
		for (Entity ent : CollisionUtil.getEntitiesAroundPoint(curr, 0.8)) {
			if (ent instanceof LivingEntity && !ent.equals(getPlayer())) {
				LivingEntity le = (LivingEntity) ent;
				bendingDamage(le, 4, 4);
				curr.getWorld().spawnParticle(Particle.CLOUD, curr, 15, 0.15, 0.15, 0.15, 0.15);
				remove();
			}
		}			
		if (blockCollide() || System.currentTimeMillis() - cooldown >= 2000) {
			curr.getWorld().spawnParticle(Particle.CLOUD, curr, 10, 0.25, 0.25, 0.25, 0.2);
			remove();
		} else {
			curr.getWorld().spawnParticle(Particle.CLOUD, curr, 50, 0.05, 0.05, 0.05, 0.1);
			curr = start.add(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist);
			dist += step;
		}	
	}

	private boolean blockCollide() {
		return curr.getBlock() == null || curr.getBlock().getType() != Material.AIR;
	}
	
	@Override
	public String getName() {
		return "Airswipe";
	}

	@Override
	public String getDescription() {
		return "Create swiping stream of compressed air, damaging and knocking back anything hit";
	}

	@Override
	public Location getLocation() {
		return this.curr;
	}
	
	@Override
	public String getDamageString() {
		return "4";
	}
	
	@Override
	public Material getMaterial() {
		return Material.PAPER;
	}
	
	@Override
	public boolean isToggleAbility() {
		return false;
	}

	@Override
	public boolean isDamageAbility() {
		return true;
	}
	
	@Override
	public int getChiCost() {
		return 25;
	}

	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
