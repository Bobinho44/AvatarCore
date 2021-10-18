package com.sanan.avatarcore.abilities.fire;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class ShieldOfFireAbility extends FireBendingAbility implements EnduringAbility {
	
	private long cooldown;
	private double rotate = 0;
	private Location lastLocation;
	private long duration;
	private int damage = 0;
	
	public ShieldOfFireAbility(BendingPlayer player) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.duration = cooldown;
		this.lastLocation = getPlayer().getLocation();
		this.start();
	}

	@Override
	public void update() {
		super.update();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.equals(getPlayer())) {
				continue;
			}
			if (getLocation().getWorld().equals(player.getWorld()) && getLocation().distance(player.getLocation()) < 2.5) {
				player.setFireTicks(20);
			}
		}
		if (System.currentTimeMillis() - duration >= 20000 || damage >= 3) {
			remove();
		}
		else if (System.currentTimeMillis() - cooldown >= 1000 || (lastLocation.getWorld().equals(getPlayer().getWorld()) && getPlayer().getLocation().distance(lastLocation) < 1)) {
			if (System.currentTimeMillis() - cooldown >= 1000) {
				cooldown = System.currentTimeMillis();
			}
			if (lastLocation.getWorld().equals(getPlayer().getWorld()) && getPlayer().getLocation().distance(lastLocation) > 1) {
				lastLocation = getPlayer().getLocation();
			}
			Location origin = getLocation();
			for (double x = 0; x < Math.PI*2; x += Math.PI/8) {
				double cos = 1.5*Math.cos(x + rotate * Math.PI/20);
				double sin = 1.5*Math.sin(x + rotate * Math.PI/20);
				sendFireParticle(new Location(getPlayer().getWorld(), origin.getX() + cos, origin.getY(), origin.getZ() + sin), 1, 0, 0, 0, 0);
				sendFireParticle(new Location(getPlayer().getWorld(), origin.getX() + cos, origin.getY() + sin, origin.getZ()), 1, 0, 0, 0, 0);
				sendFireParticle(new Location(getPlayer().getWorld(), origin.getX(), origin.getY() + cos, origin.getZ() + sin), 1, 0, 0, 0, 0);
				sendFireParticle(new Location(getPlayer().getWorld(), origin.getX() + cos, origin.getY() + sin, origin.getZ() + cos*sin/1.5), 1, 0, 0, 0, 0);
				sendFireParticle(new Location(getPlayer().getWorld(), origin.getX() + cos, origin.getY() - sin, origin.getZ() + cos*sin/1.5), 1, 0, 0, 0, 0);
			}
			rotate++;
		}
	}
	
	public void damage(int damage) {
		this.damage += damage;
	}

	@Override
	public String getName() {
		return "Shield Of Fire";
	}

	@Override
	public String getDescription() {
		return "Fire shield.";
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation().clone().add(0, 1, 0);
	}

	@Override
	public Material getMaterial() {
		return Material.SHIELD;
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
