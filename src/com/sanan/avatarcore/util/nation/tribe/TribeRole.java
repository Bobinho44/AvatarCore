package com.sanan.avatarcore.util.nation.tribe;

public enum TribeRole {

	DEFAULT, OFFICER, CO_OWNER, LEADER;
	
	public static TribeRole match(String role) {
		if (role.equalsIgnoreCase("default")) return TribeRole.DEFAULT;
		else if (role.equalsIgnoreCase("officer")) return TribeRole.OFFICER;
		else if (role.equalsIgnoreCase("co_owner")) return TribeRole.CO_OWNER;
		else if (role.equalsIgnoreCase("leader")) return TribeRole.LEADER;
		else return null;
	}

	public TribeRole getHigherLevel() {
		switch (this) {
			case DEFAULT:
				return OFFICER;
			case OFFICER:
				return CO_OWNER;
			case CO_OWNER:
			case LEADER:
				return this;
			default:
				return OFFICER;
		}
	}
	
	public TribeRole getLowerLevel() {
		switch (this) {
			case DEFAULT:
				return DEFAULT;
			case OFFICER:
				return DEFAULT;
			case CO_OWNER:
				return OFFICER;
			case LEADER:
				return LEADER;
			default:
				return DEFAULT;
		}
	}

	public boolean canBePromoted() {
		switch (this) {
			case DEFAULT:
			case OFFICER:
			default:
				return true;
			case CO_OWNER:
			case LEADER:
				return false;
		}
	}
	
	public boolean canBeDemoted() {
		switch (this) {
			case CO_OWNER:
			case OFFICER:
			default:
				return true;
			case DEFAULT:
			case LEADER:
				return false;
		}
	}
}
