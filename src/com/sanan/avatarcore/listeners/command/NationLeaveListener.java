package com.sanan.avatarcore.listeners.command;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.plot.Plot;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class NationLeaveListener implements Listener {
    private final NationManager nationManager;
    private final PlayerManager playerManager;

    public NationLeaveListener() {
        this.nationManager = NationManager.getInstance();
        this.playerManager = PlayerManager.getInstance();
    }

    @EventHandler
    public void onConfirm(AsyncPlayerChatEvent event) throws NoLoanPermittedException, ArithmeticException, StorageException, UserDoesNotExistException {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (this.nationManager.isPlayerAboutToLeave(player)) {
            if (!message.equalsIgnoreCase("confirm")) {
                return;
            }

            final BendingPlayer bendingPlayer = this.playerManager.getBendingPlayer(player);
            final BendingNation nation = this.nationManager.getPlayerNation(bendingPlayer);
            nation.removePlayer(player.getUniqueId());
            event.setCancelled(true);
            this.nationManager.removeLeaveRequest(player);
            Message.NATION_LEAVE_CONFIRMED.sendPlayer(player);
            Plot playerPlot = PlotManager.getInstance().getPlayerPlot(player);
    		if (playerPlot != null) 
    			playerPlot.sell(player);
            return;
        }
    }
}
