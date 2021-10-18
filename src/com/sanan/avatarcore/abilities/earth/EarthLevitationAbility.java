package com.sanan.avatarcore.abilities.earth;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.movingblock.MovingBlock;
import com.sanan.avatarcore.util.movingblock.MovingBlockStructure;
import com.sanan.avatarcore.util.movingblock.EarthMovingBlock.EarthLittleChunk;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthLevitationAbility extends EarthBendingAbility implements EnduringAbility {
	
	private final WallManager wm = WallManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	
	private MovingBlockStructure structure;
	private boolean evolved;
	private double distance;
	private boolean shoot;
	private Location startShootLocation;
	private boolean shootable = true;
	
	public EarthLevitationAbility(BendingPlayer player, ArrayList<Material> blocks, Location spawnLocation) {
		super(player);
		if (!player.hasFinishTutorial()) {
			getPlayer().sendMessage("§7[§6Bending Trainer§7] --> §5 Notice what would normally be your XP bar. This represents your chi. Every ability that is not passive costs some chi and if you do not have enough you will not be able to use an ability! But do not worry, your chi will naturally regenerate overtime when you are not bending!");
			shootable = false;
			new BukkitRunnable() {
				public void run() {
					getPlayer().sendMessage("§7[§6Bending Trainer§7] --> §e Anyways, now that you are holding this earth let’s test out your aim! Aim at me and left click to fire the blocks. Try and hit me I can take it!");
					shootable = true;
				}
			}.runTaskLaterAsynchronously(ac, 40);
		}
		spawnLocation.getWorld().playSound(spawnLocation, Sound.BLOCK_GRASS_BREAK, 3.0F, 0.5F);
		this.evolved = player.getLevel("earth") >= 55 ? true : false;
		this.structure = new EarthLittleChunk(blocks, player, getNewPosition(getPlayer().getLocation(), 6), evolved);
		this.distance = 0;
		this.shoot = false;
		this.start();
	}
	
	@Override
	public void update() {
		super.update();
		if (structure.getStructure().size() == 0 || distance >= 60) {
			remove();
		}
		if (!shoot) {
			getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 3));
	    	structure.translate(getPlayer().getLocation());
		}
		else {
	    	distance += 1.5;
		    // Shoot
		    if (structure.getSize() > 0) {
		    	Location curr;
				//less than 20 blocks
				if (distance <= 20) {
					curr = getNewPosition(startShootLocation, distance);
							
				//more than 20 blocks (block go down)
				} else {
					distance += 0.5;
					curr = getNewPosition(startShootLocation, distance).add(0, -0.125 * (distance - 22), 0);
				}
				structure.translate(curr);
				structure.blockCollision(player);
							
				//Hurted player
				for (LivingEntity entity : structure.entityCollision()) {
					if (!player.hasFinishTutorial() && (entity.getCustomName() == null || !entity.getCustomName().equals(getPlayer().getName()))) {
						continue;
					}
					bendingDamage(entity, evolved ? 6 : 4, 1);
					distance = 61;
		    	}
			}
	    }
	}
	
	@Override
	public void remove() {
		super.remove();
		structure.delete(player);
	}
	
	// Shoot
	public void shoot() {
		if (shootable) {
			stop();
			shoot = true;
			startShootLocation = getPlayer().getLocation();	
		}
	}
	
	// Get position of chunk on the circle around player
	private Location getNewPosition(Location location, double distance) {
		Vector vector = location.getDirection().multiply(distance);
		return location.clone().add(vector);
	}
	
	@Override
	public String getName() {
		return "Earth Levitation";
	}

	@Override
	public String getDescription() {
		return "Extract and take control of a chunk of earth, which you can throw at your enemies.";
	}

	@Override
	public Location getLocation() {
		for (MovingBlock block : structure.getStructure()) {
	    	if (wm.isFromWall(block.getLocation().getBlock().getLocation()) != null) {
	    		return block.getLocation();
	    	}
		}
		return getPlayer().getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.GRASS_BLOCK;
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
		return "4";
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
