package com.sanan.avatarcore.util.movingblock;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.tutorial.BendingTutorial;

public abstract class MovingBlockStructure {
	
	private final WallManager wm = WallManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	
	private ArrayList<MovingBlock> structure;
	
	public MovingBlockStructure() {
		structure = new ArrayList<MovingBlock>();
	}
	
	public void blockCollision(BendingPlayer shooter) {
		ArrayList<Item> dropedBlocks = new ArrayList<Item>();
		for (MovingBlock movingBlock : structure.toArray(new MovingBlock[structure.size()])) {
			if (wm.isFromWall(movingBlock.getLocation().getBlock().getLocation()) != null) return;
			if (movingBlock.getBlock().getType() == Material.AIR) { 
				structure.remove(movingBlock); 
				break;
			}
			if (movingBlock.getLocation().clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
				Item dropedBlock = movingBlock.getLocation().getWorld().dropItemNaturally(movingBlock.getLocation().clone().add(0, 1, 0), movingBlock.getBlock().clone());
				dropedBlock.setPickupDelay(999999999);
				if (!shooter.hasFinishTutorial()) {
					BendingTutorial.clearEntity(shooter.getSpigotPlayer(), dropedBlock.getEntityId());
				}
				dropedBlocks.add(dropedBlock);
				structure.remove(movingBlock);
				movingBlock.remove();
				new BukkitRunnable() {
			        public void run() {
			            for (Item dropedBlock : dropedBlocks) {
			            	dropedBlock.remove();
			            }
			        }
			    }.runTaskLaterAsynchronously(ac, 60);
			}
		}
	}
	
	public ArrayList<LivingEntity> entityCollision() {
		ArrayList<LivingEntity> entities = new ArrayList<LivingEntity>();
		for (MovingBlock movingBlock : structure) {
			for (Entity  e : movingBlock.getLocation().getChunk().getEntities()) {
	    		if (e.getLocation().distance(movingBlock.getLocation()) < 2.0) {
	    			if (e instanceof LivingEntity && !(e instanceof ArmorStand) && !entities.contains(e)) {
	    				entities.add((LivingEntity) e);
	    			}
	    		}
			}

		}
		return entities;
	}
	
	public void addMovingBlockEntity(MovingBlock movingBlock) {
		structure.add(movingBlock);
	}
	
	public void delete(BendingPlayer shooter) {
		ArrayList<Item> dropedBlocks = new ArrayList<Item>();
		for (MovingBlock movingBlock : structure.toArray(new MovingBlock[structure.size()])) {
			if (movingBlock.getBlock().getType().name().equalsIgnoreCase(Material.AIR.name()))
			{
				structure.remove(movingBlock);
				break;
			}
			Item dropedBlock = movingBlock.getLocation().getWorld().dropItemNaturally(movingBlock.getLocation().clone(), movingBlock.getBlock().clone());
			dropedBlock.setPickupDelay(999999999);
			if (!shooter.hasFinishTutorial()) {
				BendingTutorial.clearEntity(shooter.getSpigotPlayer(), dropedBlock.getEntityId());
			}
			dropedBlocks.add(dropedBlock);
			structure.remove(movingBlock);
			movingBlock.remove();
			new BukkitRunnable() {
				public void run() {
					for (Item dropedBlock : dropedBlocks) {
						dropedBlock.remove();
					}
				}
			}.runTaskLaterAsynchronously(ac, 60);
		}
	}
	
	public ArrayList<MovingBlock> getStructure() {
		return this.structure;
	}
	
	public int getSize() {
		return structure.size();
	}
	
	public abstract void translate(Location location);
}
