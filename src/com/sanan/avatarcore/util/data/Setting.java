package com.sanan.avatarcore.util.data;

import com.sanan.avatarcore.AvatarCore;

public enum Setting {

	TRIBE_MAX_CAPACITY("tribe-max-capacity"),
	NATION_MAX_CAPACITY("nation-max-capacity"),
	PLAYER_DISPLAY_NAME("player-display-name"),
	MAXIMUM_HOTBAR_NAME_LENGTH("maximum-hotbar-name-length"),
	MAIN_WORLD_NAME("main-world-name"),
	SECONDARY_WORLD_NAME("secondary-world-name");
	
	private String path;
	private AvatarCore ac = AvatarCore.getInstance();
	
	private Setting(String path) {
		this.path = path;
	}
	
	public Object get() {
		return ac.getConfig().get("settings." + this.path);
	}
	
}
