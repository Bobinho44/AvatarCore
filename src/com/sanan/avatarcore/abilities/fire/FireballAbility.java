package com.sanan.avatarcore.abilities.fire;

import org.bukkit.Location; 
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.bending.ability.fire.FireReversableAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.tutorial.BendingTutorial;

public class FireballAbility extends FireBendingAbility implements FireReversableAbility {
	
	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	private Fireball fireball;
	private long cooldown;
	
	public FireballAbility(BendingPlayer player) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.fireball = (Fireball) getPlayer().getWorld().spawnEntity(getPlayer().getLocation().add(0, 1, 0), EntityType.FIREBALL);
		this.fireball.setShooter(getPlayer());
		this.fireball.setBounce(false);
		this.fireball.setIsIncendiary(true);
		this.fireball.setDirection(this.getPlayer().getLocation().getDirection());
		this.fireball.setCustomName("FireBallAbility");
		if (!player.hasFinishTutorial()) {
			BendingTutorial.clearEntity(getPlayer(), fireball.getEntityId());
		}
		badm.addFireAbility(this);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		if (!fireball.isDead()) {
			sendFireParticle(getLocation(), 50, 0.25, 0.25, 0.25, 0.1);
		}
		stop();
	    if (fireball.isDead() || (System.currentTimeMillis() - cooldown >= 2000)) {
	    	remove();
	   	}
	}
	
	@Override
	public void remove() {
		super.remove();
		badm.removeFireAbility(this);
		if (!fireball.isDead()) {
			fireball.remove();
		}
	}
	
	@Override
	public String getName() {
		return "Fireball";
	}

	@Override
	public String getDescription() {
		return "Hurl a Fireball that explodes upon impact.";
	}

	@Override
	public Location getLocation() {
		return fireball.getLocation().clone();
	}
	
	@Override
	public String getDamageString() {
		return "5";
	}
	
	@Override
	public Material getMaterial() {
		return Material.FIRE_CHARGE;
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

	@Override
	public boolean couldReverse(Location reverser) {
		Vector a1 = reverser.getDirection().normalize();
		Vector a2 = getLocation().toVector().subtract(reverser.toVector()).normalize();
		return (Math.abs(Math.toDegrees(Math.atan2(a1.getX()*a2.getZ() - a1.getZ()*a2.getX(), a1.getX()*a2.getX() + a1.getZ()*a2.getZ()))) <= 30);
	}
	
	@Override
	public void reverse() {
		fireball.setDirection(fireball.getDirection().multiply(-1));
	}
	
}
