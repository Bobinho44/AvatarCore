package com.sanan.avatarcore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.player.nation.PlayerNationData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

@CommandAlias("nation|n")
public final class NationCommand extends BaseCommand implements Messageable {
    @Dependency
    private AvatarCore plugin;
    private final NationManager nationManager = NationManager.getInstance();
    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final TribeManager tm = TribeManager.getInstance();

    @Default
    @CommandPermission("avatarcore.nation.help")
    public void onDefault(CommandSender sender) {
        final List<String> messages = this.plugin.getConfig().getStringList("messages.nation-help");
        messages.forEach(message -> sender.sendMessage(Message.color(message)));
    }

    @Subcommand("help")
    @CommandPermission("avatarcore.nation.help")
    public void help(CommandSender sender) {
        this.onDefault(sender);
    }

    @Subcommand("join")
    @CommandPermission("avatarcore.nation.join")
    @CommandCompletion("Fire|Earth|Air|Water")
    @Syntax("[nation name]")
    public void join(Player player, @Single String nationName) {
        final BendingPlayer bPlayer = this.playerManager.getBendingPlayer(player);
        final BendingNation nation = this.nationManager.getNation(nationName);
        if (!this.nationManager.isRegistered(nation)) {
            Message.NATION_NOT_EXIST.sendPlayer(player);
            return;
        }

        if (this.nationManager.playerHasNation(bPlayer)) {
            Message.PLAYER_ALREADY_IN_NATION.sendPlayer(player);
            return;
        }

        if (nation.getPlayerCount() >= nation.getMaxCapacity()) {
            Message.NATION_FULL.sendPlayer(player);
            return;
        }

        final PlayerNationData playerNationData = bPlayer.getPlayerNationData();
        final TextPlaceholder nationChosenPlaceholder = TextPlaceholder.of("%nation%", playerNationData.getNationChosen());
        if (playerNationData.hasNationChosen() && !playerNationData.getNationChosen().equalsIgnoreCase(nation.getName())) {
            this.sendColoredMessage(player, Message.NATION_CANNOT_BE_CHANGED.getMessage(), nationChosenPlaceholder);
            return;
        }

        if (playerNationData.getTimesChosen() >= 3) {
            Message.NATION_CHANGED_TOO_OFTEN.sendPlayer(player);
            return;
        }
        
        if (tm.playerHasTribe(bPlayer)) {
        	if (tm.getTribe(bPlayer).getClaimNumber() > BendingTribe.NATION_TRIBE_MAX_CLAIMS) {
	            player.sendMessage("You have over 20 claims in your tribe!");
	            return;
        	}
        	else if (tm.getTribe(bPlayer).getMembers().size() > BendingTribe.NATION_TRIBE_MAX_PLAYERS) {
	            player.sendMessage("You have over 5 members in your tribe!");
	            return;
        	}
        }
        
        nation.addPlayer(bPlayer);
        bPlayer.updateNationData(nation.getName());
        this.sendColoredMessage(player, Message.NATION_JOIN.getMessage(), TextPlaceholder.of("%nation%", nation.getName()));
    }

    @Subcommand("leave")
    @CommandPermission("avatarcore.nation.leave")
    public void leave(Player player) {
        final BendingPlayer bendingPlayer = this.playerManager.getBendingPlayer(player);
        if (!this.nationManager.playerHasNation(bendingPlayer)) {
            this.sendColoredMessage(player, "&cYou must join a nation in order to use this command. &e/nation help &cfor a list of commands.");
            return;
        }
        final PlayerNationData playerNationData = bendingPlayer.getPlayerNationData();
        if (playerNationData.getTimesChosen() >= 3) {
        	Message.NATION_CHANGED_TOO_OFTEN.sendPlayer(player);
        	return;
        }
        final BendingNation nation = this.nationManager.getPlayerNation(bendingPlayer);
        final TextPlaceholder nationPlaceholder = TextPlaceholder.of("%nation%", nation.getName());
        this.sendColoredMessage(player, Message.NATION_LEAVE.getMessage(), nationPlaceholder);
        this.nationManager.addLeaveRequest(player);
    }

    @Subcommand("nations|n")
    @CommandPermission("avatarcore.nation.nations")
    public void nationsInfo(CommandSender sender) {
        for (BendingNation nation : this.nationManager.getNations()) {
            this.sendNationInfoMessage(sender, nation);
        }

        this.sendColoredMessage(sender, "&eRogues: &a" + this.nationManager.getRogues().size());
    }

    private void sendNationInfoMessage(CommandSender sender, BendingNation nation) {
        final String name = nation.getName();
        final int playersCount = nation.getPlayerCount();
        final int capacity = nation.getMaxCapacity();
        this.sendColoredMessage(sender, "&b" + name + "&8: &a" + playersCount + "&8/&7" + capacity);
    }

    @Subcommand("show")
    @CommandPermission("avatarcore.nation.show")
    @Syntax("[nation or player name]")
    public void show(CommandSender sender, String argument) {
        final BendingNation nation = this.nationManager.getNation(argument);
        if (nation != null) {
            this.sendDetailedNationInfoMessage(sender, nation);
        } else {
            final Player bukkitPlayer = Bukkit.getPlayer(argument);
            if (bukkitPlayer == null) {
                Message.NATION_SHOW_INVALID.sendSender(sender);
                return;
            }

            final BendingPlayer targetPlayer = this.playerManager.getBendingPlayer(bukkitPlayer);
            if (!this.nationManager.playerHasNation(targetPlayer)) {
                Message.TARGET_NOT_IN_NATION.sendSender(sender);
                return;
            }

            this.sendDetailedNationInfoMessage(sender, this.nationManager.getPlayerNation(targetPlayer));
        }
    }

    private void sendDetailedNationInfoMessage(CommandSender sender, BendingNation nation) {
        final TextPlaceholder name = TextPlaceholder.of("%nation%", nation.getName());
        final TextPlaceholder playerCount = TextPlaceholder.of("%players%", Integer.toString(nation.getPlayerCount()));
        final TextPlaceholder onlinePlayerNames = TextPlaceholder.of("%online%", String.join(", ", nation.getOnlinePlayers().toArray(new String[0])));
        final TextPlaceholder offlinePlayerNames = TextPlaceholder.of("%offline%", String.join(", ", nation.getOfflinePlayers().toArray(new String[0])));
        final TextPlaceholder leaderStatus = TextPlaceholder.of("%leader%", nation.isLeaderOnline() ? "&aONLINE" : "&cOFFLINE");
        final List<String> message = this.plugin.getConfig().getStringList("messages.nation-show");
        this.sendColoredList(sender, this.messagesWithReplacements(message, name, playerCount, onlinePlayerNames, offlinePlayerNames, leaderStatus));
    }

    @Subcommand("assign")
    @CommandPermission("avatarcore.nation.assign")
    @Syntax("[player name] [nation]")
    public void assign(CommandSender sender, OnlinePlayer targetPlayer, String targetNation) {
        final BendingNation nation = this.nationManager.getNation(targetNation);
        if (nation == null) {
            Message.NATION_NOT_EXIST.sendSender(sender);
            return;
        }

        final Player bukkitPlayer = targetPlayer.getPlayer();
        final BendingPlayer bendingPlayer = this.playerManager.getBendingPlayer(bukkitPlayer);
        if (nation.getLeaderUUID().equalsIgnoreCase(bukkitPlayer.getUniqueId().toString())) {       //TODO: uuid as string (should be uuid)
            Message.TARGET_NATION_LEADER.sendSender(sender);
            return;
        }

        if (nation.getPlayerCount() >= nation.getMaxCapacity()) {
            Message.NATION_FULL.sendSender(sender);
            return;
        }

        this.nationManager.clearPlayerNations(bendingPlayer);
        nation.addPlayer(bendingPlayer);
        this.sendColoredMessage(sender, Message.NATION_ASSIGNED.getMessage(), TextPlaceholder.of("%nation%", nation.getName()));
    }

    @Subcommand("kick")
    @CommandPermission("avatarcore.nation.kick")
    @Syntax("[player name] (reason)")
    public void kick(CommandSender sender, OnlinePlayer targetPlayer, @Optional String reason) {
        final Player targetBukkitPlayer = targetPlayer.getPlayer();
        final BendingPlayer bendingPlayer = this.playerManager.getBendingPlayer(targetBukkitPlayer);
        if (!this.nationManager.playerHasNation(bendingPlayer)) {
            Message.TARGET_ROGUE.sendSender(sender);
            return;
        }

        final BendingNation nation = this.nationManager.getPlayerNation(bendingPlayer);
        if (nation.getLeaderUUID().equalsIgnoreCase(targetBukkitPlayer.getUniqueId().toString())) {     //TODO: uuid as string (should be uuid)
            Message.TARGET_NATION_LEADER.sendSender(sender);
            return;
        }

        if (reason == null) {
            reason = "none";
        }

        nation.kick(bendingPlayer);
        Message.NATION_KICK.sendSender(sender);
        this.sendColoredMessage(targetBukkitPlayer, Message.NATION_KICKED.getMessage(),
                TextPlaceholder.of("%nation%", nation.getName()),
                TextPlaceholder.of("%reason%", reason));
    }
}
