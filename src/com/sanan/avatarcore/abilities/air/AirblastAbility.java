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

public class AirblastAbility extends AirBendingAbility {
	
	private Location start;
	private Location curr;
	private long cooldown;
	private double step;
	private double dist;
	
	
	private Vector dir;
	
	public AirblastAbility(BendingPlayer player, double step) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.step = step;
		this.start = player.getSpigotPlayer().getLocation().add(0, 1, 0);
		this.dir = player.getSpigotPlayer().getLocation().getDirection().normalize();
		this.curr = start.add(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist);
		this.dist += step;
		this.start();
	}

	@Override
	public void update() {
		super.update();
		stop();
		for (Entity ent : CollisionUtil.getEntitiesAroundPoint(curr, 0.8)) {
			if (ent instanceof LivingEntity && !ent.equals(getPlayer())) {
				if (!player.hasFinishTutorial() && (ent.getCustomName() == null || !ent.getCustomName().equals(getPlayer().getName()))) {
					continue;
				}
				LivingEntity le = (LivingEntity) ent;
				bendingDamage(le, 5, 4);
				spawnParticle(10, 0.25, 0.2);
				remove();
			}
		}	
		if (blockCollide() || System.currentTimeMillis() - cooldown >= 2000) {
			spawnParticle(10, 0.25, 0.2);
			remove();
		} else {
			spawnParticle(50, 0.05, 0.1);
			curr = start.add(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist);
			dist += step;
		}
	}
	
	private void spawnParticle(int size, double offset, double extra) {
		if (player.hasFinishTutorial()) {
			curr.getWorld().spawnParticle(Particle.CLOUD, curr, size, offset, offset, offset, extra);
		}
		else {
			getPlayer().spawnParticle(Particle.CLOUD, curr, size, offset, offset, offset, extra);
		}
	}
	private boolean blockCollide() {
		return curr.getBlock() == null || curr.getBlock().getType() != Material.AIR;
	}
	
	@Override
	public String getName() {
		return "Airblast";
	}

	@Override
	public String getDescription() {
		return "Create a whirling stream of air that propels players back and deals damage.";
	}

	@Override
	public Location getLocation() {
		return this.curr;
	}
	
	@Override
	public String getDamageString() {
		return "5";
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
		return 35;
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
