package com.sanan.avatarcore.util.nation.tribe;

import java.util.UUID;

import org.bukkit.Bukkit;

public final class TribeMember {
	
    private final UUID uuid;
    private String playerName;
    private TribeRole role;
    private boolean inAutoClaim;
    
    public TribeMember(UUID uuid) {
        this.uuid = uuid;
        this.playerName = Bukkit.getOfflinePlayer(uuid).getName();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public TribeRole getRole() {
        return this.role;
    }

    public void setRole(TribeRole role) {
        this.role = role;
    }
    
    public boolean isInAutoClaim() {
    	return this.inAutoClaim;
    }
    
    public void activeAutoClaim(boolean inAutoClaim) {
    	this.inAutoClaim = inAutoClaim;
    }
}
