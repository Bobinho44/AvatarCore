package com.sanan.avatarcore.abilities.fire;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireReversableAbility;
import com.sanan.avatarcore.util.collision.CollisionUtil;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class DragonBreathAbility extends FireBendingAbility implements FireReversableAbility {
	
	private final BendingAbilityManager bam = BendingAbilityManager.getInstance();
	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	private int distance;
	private long lastDamageStamp;
	private Location actualPosition;
	private Location launchedLocation;
	
	public DragonBreathAbility(BendingPlayer player, int distance) {
		super(player);
		this.distance = distance;
		this.lastDamageStamp = System.currentTimeMillis();
		this.launchedLocation = getPlayer().getLocation();
		this.actualPosition =  getPlayer().getLocation();
		badm.addFireAbility(this);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		List<Block> line = getPlayer().getLineOfSight(null, this.distance);
		
		getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
		
		for (int i=0; i<line.size() && i >= 0; i++) {
			int particleCount = (i*8) + 5;
			double particleSpread = 0.005*i;
			double particleSpeed = 0.01*i;
			double fireRadius = 0.25*i;
			actualPosition = line.get(i).getLocation();
			sendFireParticle(actualPosition.clone().add(0.5, 0.5, 0.5), particleCount, particleSpread, particleSpread, particleSpread, particleSpeed);
			for (Block b : CollisionUtil.getBlocksAroundPoint(actualPosition.clone().add(0.7, 0.7, 0.7), fireRadius)) {
				if (!b.isEmpty() && !b.isLiquid() && !b.isPassable() && b.getLocation().add(0, 1, 0).getBlock().isEmpty()) {
					b.getLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
				}
			}
		}
		if (System.currentTimeMillis() - lastDamageStamp >= 500) {
			Block middleBlock = line.get((int)line.size()/2);
			for (Entity ent : getPlayer().getWorld().getNearbyEntities(middleBlock.getLocation().add(0.5,0.5,0.5), 7, 7, 7)) {
				if (ent instanceof LivingEntity && !ent.equals(getPlayer())) {
					Vector a1 = launchedLocation.getDirection().normalize();
					Vector a2 = ent.getLocation().toVector().subtract(launchedLocation.toVector()).normalize();
					if (Math.abs(Math.toDegrees(Math.atan2(a1.getX()*a2.getZ() - a1.getZ()*a2.getX(), a1.getX()*a2.getX() + a1.getZ()*a2.getZ()))) <= 45) {
						LivingEntity le = (LivingEntity)ent;
						bendingDamage(le, getDamage(le), 0);
						le.setFireTicks(20);
					}
				}
			}
			lastDamageStamp = System.currentTimeMillis();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		badm.removeFireAbility(this);
	}
	
	private double getDamage(LivingEntity le) {
		float additionnalDamage = badm.getChargedDamage(getPlayer());
		return (bam.isPlayerUsingAbility(player, "Blue Fire") ? 3 : 2.5) + additionnalDamage;
	}
	
	@Override
	public String getName() {
		return "Dragon Breath";
	}

	@Override
	public String getDescription() {
		return "Focus an immense amount of Chi in your lungs to create a scorching stream of fire from your mouth, burning down everything in its path...";
	}

	@Override
	public Location getLocation() {
		return actualPosition;
	}

	@Override
	public Material getMaterial() {
		return Material.DRAGON_HEAD;
	}
	
	@Override
	public String getDamageString() {
		return "5/s";
	}

	@Override
	public boolean isToggleAbility() {
		return true;
	}

	@Override
	public boolean isDamageAbility() {
		return true;
	}
	
	@Override
	public int getChiCost() {
		return 15;
	}
	
	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 50;
	}

	@Override
	public boolean couldReverse(Location reverser) {
		Vector a1 = reverser.getDirection().normalize();
		Vector a2 = actualPosition.toVector().subtract(reverser.toVector()).normalize();
		return (Math.abs(Math.toDegrees(Math.atan2(a1.getX()*a2.getZ() - a1.getZ()*a2.getX(), a1.getX()*a2.getX() + a1.getZ()*a2.getZ()))) <= 45);
	}

	@Override
	public void reverse() {
		bendingDamage(getPlayer(), getDamage(getPlayer()), 0);
		getPlayer().setFireTicks(20);
		remove();
	}
	
}
