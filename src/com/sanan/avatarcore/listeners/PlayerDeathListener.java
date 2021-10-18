package com.sanan.avatarcore.listeners;

import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerDeathListener implements Listener {
    private final PlayerManager playerManager = PlayerManager.getInstance();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final BendingPlayer bendingPlayer = this.playerManager.getBendingPlayer(player);
        if (bendingPlayer != null) {
	        final int currentPower = bendingPlayer.getPlayerPower().getPowerValue();
	        if (currentPower <= -10) {
	            return;
	        }
	        if (currentPower == -9) 
	            bendingPlayer.decreasePlayerPowerBy(1);
	        else 
	            bendingPlayer.decreasePlayerPowerBy(2);
        }
    }
}
