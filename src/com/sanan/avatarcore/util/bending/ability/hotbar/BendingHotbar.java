package com.sanan.avatarcore.util.bending.ability.hotbar;

import com.sanan.avatarcore.util.bending.ability.BendingAbility;

public class BendingHotbar {

	private String name;
	private BendingAbility[] abilities;
	private boolean isRenaming;
	
	public BendingHotbar(String name) {
		this.name = name;
		abilities = new BendingAbility[9];
		this.isRenaming = false;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRenaming(boolean isRenaming) {
		this.isRenaming = isRenaming;
	}
	
	public boolean isRenaming() {
		return isRenaming;
	}
	public BendingAbility[] getAbilities() {
		return this.abilities;
	}
	public void setAbility(int index, BendingAbility ability) {
		if (index >= this.abilities.length) return;
		this.abilities[index] = ability;
	}
	
}
