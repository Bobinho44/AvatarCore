package com.sanan.avatarcore.util.player.power;

import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class PowerChargingTask extends BukkitRunnable {
    private final PlayerManager playerManager = PlayerManager.getInstance();

    @Override
    public void run() {
        for (BendingPlayer player : playerManager.getPlayers()) {
            if (player.getPlayerPower().getPowerValue() < 10) {
            	player.increasePlayerPower();
            }
        }
    }
}
