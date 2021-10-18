package com.sanan.avatarcore.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;

public class ObsidianListener implements Listener {

	private final AvatarCore ac = AvatarCore.getInstance();
	
	Map<Location, Integer> breakObsidian = new HashMap<Location, Integer>();
	
	private int[] to3D(int idx) {
		final int z = idx / (9);
		idx -= (z * 9);
		final int y = idx / 3;
		final int x = idx % 3;
		return new int[]{ x, y, z };
	}

	private List<Block> getExplodeBlock(Location tnt) {
		Location start = tnt.clone().add(-1, -1, -1);
		List<Block> blocks = new ArrayList<Block>();
		for (int i = 0; i < 27; i++) {
			int[] coord = to3D(i);
			Location loc = start.clone().add(coord[0]*1, coord[1]*1, coord[2]*1);
			blocks.add(tnt.getWorld().getBlockAt(loc));
		}
		return blocks;
	}
	
	@EventHandler
	public void onFireBallExplode(EntityExplodeEvent event) {
		if (event.getEntityType() == EntityType.PRIMED_TNT) {
			for (Block block : getExplodeBlock(event.getLocation())) {
				if (block.getType() == Material.OBSIDIAN || block.getType() == Material.CRYING_OBSIDIAN) {
					Integer key = breakObsidian.get(block.getLocation());
					if (key != null && key == 7) {
						breakObsidian.remove(block.getLocation());
						block.setType(Material.AIR);
					}
					else {
						breakObsidian.put(block.getLocation(), key == null ? 1 : key + 1);
						new BukkitRunnable() {
							public void run() {
								breakObsidian.remove(block.getLocation());
							}
						}.runTaskLaterAsynchronously(ac, 72000);
					}
				}
			}
		}
	}
}
