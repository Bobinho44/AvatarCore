package com.sanan.avatarcore.util.movingblock.EarthMovingBlock;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import com.sanan.avatarcore.util.movingblock.MovingBlockEntity;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.tutorial.BendingTutorial;

public class EarthMovingBlockEntity extends MovingBlockEntity {

	public EarthMovingBlockEntity(Location location, BendingPlayer player) {
		super((ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND));
		getEntity().setGravity(false);
		if (!player.hasFinishTutorial()) {
			BendingTutorial.clearEntity(player.getSpigotPlayer(), getEntity().getEntityId());
		}
	}

}
