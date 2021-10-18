package com.sanan.avatarcore.util.movingblock.wallMovingBlock;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import com.sanan.avatarcore.util.movingblock.MovingBlockEntity;

public class WallMovingBlockEntity extends MovingBlockEntity {

	private float maxLife;
	
	public WallMovingBlockEntity(Location location, float maxLife) {
		super(location.getWorld().spawn(location, ArmorStand.class));
		getEntity().setGravity(false);
		this.maxLife = maxLife;
		setLife(maxLife);
	}
	
	public void setLife(float health) {
		String life = "";
		for (int i = 0; i < maxLife/2; i++) {
			life += i < health/2 ? "§c❤" : "§7❤";
		}
		setName(life);
	}

}
