package com.sanan.avatarcore.util.bending.ability.fire;

import org.bukkit.Location;

public interface FireReversableAbility {

	public boolean couldReverse(Location reverser);
	
	public void reverse();
}
