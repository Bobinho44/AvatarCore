package com.sanan.avatarcore.util.nation;

import com.sanan.avatarcore.util.nation.tribe.BendingTribe;

public class BendingInviteRequest {
	
	private final BendingTribe requester;
	
	public BendingInviteRequest(BendingTribe requester) {
		this.requester = requester;
	}
	
	public BendingTribe getRequester() {
		return requester;
	}

}
