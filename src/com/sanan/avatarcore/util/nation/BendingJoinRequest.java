package com.sanan.avatarcore.util.nation;

import com.sanan.avatarcore.util.player.BendingPlayer;

public class BendingJoinRequest {
	
	private final BendingPlayer requester;
	
	public BendingJoinRequest(BendingPlayer requester) {
		this.requester = requester;
	}
	
	public BendingPlayer getRequester() {
		return requester;
	}

}
