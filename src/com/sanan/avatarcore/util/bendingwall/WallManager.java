package com.sanan.avatarcore.util.bendingwall;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class WallManager {

	private static WallManager instance;
	
	private static List<BendingWall> walls = new ArrayList<BendingWall>();
	
	public static WallManager getInstance() {
		if (instance == null) {
			instance = new WallManager();
		}
		return instance;
	}
	
	public List<BendingWall> getWalls() {
		return walls;
	}
	
	public BendingWall isFromWall(Location location) {
		for (BendingWall wall : walls) {
			if (wall.getBlocks().get(location) != null)
				return wall;
		}
		return null;
	}
	
	public void createWall(BendingWall wall) {
		walls.add(wall);
	}
	
	public void destroyWall(BendingWall wall) {
		walls.remove(wall);
	}
}
