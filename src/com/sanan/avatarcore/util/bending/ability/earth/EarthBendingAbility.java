package com.sanan.avatarcore.util.bending.ability.earth;

import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public abstract class EarthBendingAbility extends CoreBendingAbility {

	public EarthBendingAbility(BendingPlayer player) {
		super(player);
	}

	@Override
	public BendingElement getElement() {
		return BendingElement.EARTH;
	}
	
	
}
