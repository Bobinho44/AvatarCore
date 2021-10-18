package com.sanan.avatarcore.util.bending.ability.water;

import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public abstract class WaterBendingAbility extends CoreBendingAbility {

	public WaterBendingAbility(BendingPlayer player) {
		super(player);
	}

	@Override
	public BendingElement getElement() {
		return BendingElement.WATER;
	}
	
}
