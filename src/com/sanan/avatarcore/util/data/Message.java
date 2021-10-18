package com.sanan.avatarcore.util.data;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.AvatarCore;

import net.md_5.bungee.api.ChatColor;

public enum Message {

	TELEPORTATION_CANCELLED("teleportation-cancelled"),
	TELEPORTATION_COOLDOWN("teleportation-cooldown"),
	INVALID_PERMISSIONS("invalid-permissions"),
	TRIBE_LEAVE("tribe-leave"),
	TRIBE_LEAVE_GLOBAL("tribe-leave-global"),
	TRIBE_OWNER_LEAVE("tribe-owner-leave"),
	NOT_IN_TRIBE("not-in-tribe"),
	TRIBE_NOT_EXIST("tribe-not-exist"),
	TRIBE_PERMISSION_COMMAND("tribe-permission-command"),
	TRIBE_OWNER_COMMAND("tribe-owner-command"),
	NATION_NOT_EXIST("nation-not-exist"),
	ALREADY_IN_TRIBE("already-in-tribe"),
	TRIBE_NAME_TAKEN("tribe-name-taken"),
	TRIBE_NAME_LONG("tribe-name-long"),
	TRIBE_CREATE("tribe-create"),
	TRIBE_ALREADY_REQUESTED("tribe-already-requested"),
	TRIBE_REQUEST("tribe-request"),
	TRIBE_REQUEST_GLOBAL("tribe-request-global"),
	TRIBE_JOIN("tribe-join"),
	TRIBE_JOIN_GLOBAL("tribe-join-global"),
	TRIBE_FULL("tribe-full"),
	TRIBE_OFFICER_COMMAND("tribe-officer-command"),
	TARGET_NOT_ONLINE("target-not-online"),
	TARGET_NOT_IN_TRIBE("target-not-in-tribe"),
	TARGET_NOT_IN_NATION("target-not-in-nation"),
	TARGET_CANNOT_PROMOTE("target-cannot-promote"),
	TARGET_CANNOT_DEMOTE("target-cannot-demote"),
	TARGET_IN_TRIBE("target-in-tribe"),
	TRIBE_PROMOTE("tribe-promote"),
	TRIBE_PROMOTED("tribe-promoted"),
	TRIBE_PROMOTE_GLOBAL("tribe-promote-global"),
	TRIBE_DEMOTE("tribe-demote"),
	TRIBE_DEMOTED("tribe-demoted"),
	TRIBE_DEMOTE_GLOBAL("tribe-demote-global"),
	TRIBE_SETHOME("tribe-sethome"),
	TRIBE_SETHOME_OUTSIDE("tribe-sethome-outside"),
	PLAYER_ALREADY_IN_NATION("player-already-in-nation"),
	PLAYER_ALREADY_INVITED("player-already-invited"),
	PLAYER_INVITED("player-invited"),
	PLAYER_INVITED_GLOBAL("player-invited-global"),
	TARGET_CANNOT_KICK("target-cannot-kick"),
	TRIBE_KICK("tribe-kick"),
	TRIBE_KICKED("tribe-kicked"),
	TRIBE_KICK_GLOBAL("tribe-kick-global"),
	TRIBE_DISBAND_CONFIRM("tribe-disband-confirm"),
	NATION_LEAVE("nation-leave"),
	NATION_LEAVE_CONFIRMED("nation-leave-confirmed"),
	NATION_SHOW_INVALID("nation-show-invalid"),
	NATION_ASSIGNED("nation-assigned"),
	NATION_FULL("nation-full"),
	NATION_KICK("nation-kick"),
	NATION_KICKED("nation-kicked"),
	NATION_JOIN("nation-join"),
	NATION_CANNOT_BE_CHANGED("nation-cannot-be-changed"),
	NATION_CHANGED_TOO_OFTEN("nation-changed-too-often"),
	TARGET_NATION_LEADER("target-nation-leader"),
	TARGET_ROGUE("target-rogue"),
	TRIBE_DISBAND_GLOBAL("tribe-disband-global"),
	TRIBE_NO_HOME("tribe-no-home"),
	UNKNOWN_TRIBE_COMMAND("unknown-tribe-command"),
	PLOT_NO_PLOT("plot-no-plot"),
	PLOT_SOLD("plot-sold"),
	PLOT_SETHOME_SUCCESS("plot-sethome-success"),
	PLOT_SETHOME_FAIL("plot-sethome-fail"),
	NO_BUY_OTHER_NATION_PLOT("no-buy-other-nation-plot"),
	ALREADY_HAVE_PLOT("already-have-plot"),
	ALREADY_SOLD_PLOT("already-sold-plot"),
	NOT_ENOUGH_BUY_PLOT("not-enough-buy-plot"),
	BUY_PLOT_SUCCESS("buy-plot-success");
	
	private String path;
	private AvatarCore ac = AvatarCore.getInstance();
	
	Message(String path) {
		this.path = path;
	}

	public void sendSender(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ac.getConfig().getString("messages." + this.path)));
	}
	
	public void sendPlayer(Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', ac.getConfig().getString("messages." + this.path)));
	}
	
	public String getMessage() {
		return ac.getConfig().getString("messages." + this.path);
	}
	
	public static String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
}
