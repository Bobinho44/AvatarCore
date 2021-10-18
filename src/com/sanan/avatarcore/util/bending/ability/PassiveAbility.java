package com.sanan.avatarcore.util.bending.ability;

import com.sanan.avatarcore.abilities.air.passive.AirCushionAbility;
import com.sanan.avatarcore.abilities.air.passive.AirPunchAbility;
import com.sanan.avatarcore.abilities.air.passive.EnhancedAgilityAbility;
import com.sanan.avatarcore.abilities.air.passive.EnhancedSpeedAbility;
import com.sanan.avatarcore.abilities.earth.passive.CaveSightAbility;
import com.sanan.avatarcore.abilities.earth.passive.HeartyAbility;
import com.sanan.avatarcore.abilities.fire.passive.BlueFireAbility;
import com.sanan.avatarcore.abilities.fire.passive.ChargedAttacksAbility;
import com.sanan.avatarcore.abilities.fire.passive.CookAbility;
import com.sanan.avatarcore.abilities.fire.passive.FireResistanceAbility;
import com.sanan.avatarcore.abilities.fire.passive.IgniteAbility;
import com.sanan.avatarcore.abilities.water.passive.BubbleAbility;
import com.sanan.avatarcore.abilities.water.passive.DepthStriderAbility;
import com.sanan.avatarcore.abilities.water.passive.FishAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public interface PassiveAbility {
	
	public static void createPassiveAbilityInstance(BendingPlayer bPlayer, BendingAbility passiveAbility) {
		if (passiveAbility instanceof AirCushionAbility)
			new AirCushionAbility(bPlayer);
		else if (passiveAbility instanceof AirPunchAbility)
			new AirPunchAbility(bPlayer);
		else if (passiveAbility instanceof EnhancedAgilityAbility)
			new EnhancedAgilityAbility(bPlayer);
		else if (passiveAbility instanceof EnhancedSpeedAbility)
			new EnhancedSpeedAbility(bPlayer);
		if (passiveAbility instanceof CaveSightAbility)
			new CaveSightAbility(bPlayer);
		else if (passiveAbility instanceof HeartyAbility)
			new HeartyAbility(bPlayer);
		else if (passiveAbility instanceof BlueFireAbility)
			new BlueFireAbility(bPlayer);
		else if (passiveAbility instanceof ChargedAttacksAbility)
			new ChargedAttacksAbility(bPlayer);
		else if (passiveAbility instanceof FireResistanceAbility)
			new FireResistanceAbility(bPlayer);
		//else if (passiveAbility instanceof FlameRedirectionAbility)
			//new FlameRedirectionAbility(bPlayer);
		else if (passiveAbility instanceof CookAbility)
			new CookAbility(bPlayer);
		else if (passiveAbility instanceof IgniteAbility)
			new IgniteAbility(bPlayer);
		else if (passiveAbility instanceof BubbleAbility)
			new BubbleAbility(bPlayer);
		else if (passiveAbility instanceof DepthStriderAbility)
			new DepthStriderAbility(bPlayer);
		else if (passiveAbility instanceof FishAbility)
			new FishAbility(bPlayer);
	}
	
}
