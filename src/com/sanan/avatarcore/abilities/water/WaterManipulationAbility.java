package com.sanan.avatarcore.abilities.water;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.bendingwall.BendingWall;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class WaterManipulationAbility extends WaterBendingAbility implements EnduringAbility {
	
	private final WallManager wm = WallManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	
	private Location position;
	private BlockData data;
	private int start;
	private boolean shoot;
	private double distance;
	private Location startShootLocation;
	private boolean shootable = true;
	
	public WaterManipulationAbility(BendingPlayer player) {
		super(player);
		if (!player.hasFinishTutorial()) {
			getPlayer().sendMessage("§7[§6Bending Trainer§7] --> §5 Notice what would normally be your XP bar. This represents your chi. Every ability that is not passive costs some chi and if you do not have enough you will not be able to use an ability! But do not worry, your chi will naturally regenerate overtime when you are not bending!");
			shootable = false;
			new BukkitRunnable() {
				public void run() {
					getPlayer().sendMessage("§7[§6Bending Trainer§7] --> §e Anyways, now that you are holding this water, let’s test out your aim! Aim at me and left click to propel your water forward! Try and hit me I can take it!");
					shootable = true;
				}
			}.runTaskLaterAsynchronously(ac, 40);
		}
		this.setNewPosition(getPlayer().getLocation(), 3);
		while (position.getBlock().getType() != Material.AIR) {
			position.add(0, 1, 0);
		}
		this.data = this.position.getBlock().getBlockData();
		this.shoot = false;
		this.distance = 0;
		this.start = 0;
		this.start();
	}

	@Override
	public void update() {
		super.update();
		if (!shoot) {
			setBlock(position, data);
			setNewPosition(getPlayer().getLocation(), 3);
			if (position.getBlock().getType() == Material.AIR && start == 0)  {
			  	start = 1;
			}
			if (position.getBlock().getType() != Material.AIR && start == 0) {
				while (position.getBlock().getType() != Material.AIR) {
					position.add(0, 1, 0);
				}
			}
			else if (position.getBlock().getType() != Material.AIR) {
				if (!player.hasFinishTutorial()) {
					getPlayer().sendMessage("§7[§6Bending Trainer§7] --> §eOh no! It looks like you lost your water! No worries, go ahead and pick up some more!");
				}
				this.remove();
				return;
			}
			data = position.getBlock().getBlockData();
			setBlock(position, Material.WATER.createBlockData());
		}
		else {
			distance += 1.5;
	    	
	    	// Stop shoot (end)
	    	if (distance >= 60) {
	    		remove();
	    	}
	    	
	    	// Shoot
	    	else if (data.getMaterial() == Material.AIR) {
	    		setBlock(position, data);
				//less than 20 blocks
				if (distance <= 20) {
					setNewPosition(startShootLocation, distance);
				} 
				else {
					distance += 0.5;
					setNewPosition(startShootLocation.add(0, -0.125 * (distance - 22), 0), distance);
				}
				data = position.getBlock().getBlockData();
				setBlock(position, Material.WATER.createBlockData());
				
				//Hurted entity
				for (Entity  entity : position.getChunk().getEntities()) {
			    	if (entity.getLocation().distance(position) < 2.0) {
			    		if (entity instanceof LivingEntity && !entity.equals(getPlayer())) {
			    			if (!player.hasFinishTutorial() && (entity.getCustomName() == null || !entity.getCustomName().equals(getPlayer().getName()))) {
								continue;
							}
							bendingDamage((LivingEntity) entity, 5, 1);
							distance = 61;
			    		}
					}
				}
	    	}
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		BendingWall otherWall = wm.isFromWall(getLocation().getBlock().getLocation());
    	if (otherWall == null) {
    		setBlock(position, data);
    		position.getBlock().getState().update(true);
    	}
	}
	
	public void shoot() {
		if (shootable) {
			stop();
			shoot = true;
			startShootLocation = getPlayer().getLocation();	
		}
	}
	
	private void setBlock(Location position, BlockData data) {
		if (player.hasFinishTutorial()) {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(position, data));
		}
		else {
			getPlayer().sendBlockChange(position, data);
		}
	}
	private void setNewPosition(Location location, double distance) {
		Vector vector = location.getDirection().multiply(distance);
		Location finalLocation = location.clone().add(vector);
		position = new Location(location.getWorld(), finalLocation.getX(), Math.ceil(finalLocation.getY()), finalLocation.getZ()).getBlock().getLocation();
	}
	    
	@Override
	public String getName() {
		return "Water Manipulation";
	}

	@Override
	public String getDescription() {
		return "Raise a bubble of water allowing you to.. TBC";
	}

	@Override
	public Location getLocation() {
		return position;
	}

	@Override
	public Material getMaterial() {
		return Material.WATER_BUCKET;
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
	public String getDamageString() {
		return "5";
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
