package com.sanan.avatarcore.util.bending.ability.air;

import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public abstract class AirBendingAbility extends CoreBendingAbility {

	public AirBendingAbility(BendingPlayer player) {
		super(player);
	}

	@Override
	public BendingElement getElement() {
		return BendingElement.AIR;
	}
	
}
