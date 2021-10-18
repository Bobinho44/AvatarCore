package com.sanan.avatarcore.util.collision;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CollisionUtil {

	public static List<Block> getBlocksAroundPoint(final Location location, final double radius) {
		final List<Block> blocks = new ArrayList<Block>();

		final int xorg = location.getBlockX();
		final int yorg = location.getBlockY();
		final int zorg = location.getBlockZ();

		final int r = (int) radius * 4;

		for (int x = xorg - r; x <= xorg + r; x++) {
			for (int y = yorg - r; y <= yorg + r; y++) {
				for (int z = zorg - r; z <= zorg + r; z++) {
					final Block block = location.getWorld().getBlockAt(x, y, z);
					if (block.getLocation().distanceSquared(location) <= radius * radius) {
						blocks.add(block);
					}
				}
			}
		}
		return blocks;
	}
	
	public static List<Entity> getEntitiesAroundPoint(final Location location, final double radius) {
		return new ArrayList<>(location.getWorld().getNearbyEntities(location, radius, radius, radius, entity -> !(entity.isDead() || (entity instanceof Player && ((Player) entity).getGameMode().equals(GameMode.SPECTATOR)))));
	}
	
}
