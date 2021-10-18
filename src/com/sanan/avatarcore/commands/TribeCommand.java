package com.sanan.avatarcore.commands;

import co.aikar.commands.BaseCommand; 
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.listeners.command.TribeDisbandListener;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.Claim;
import com.sanan.avatarcore.util.nation.tribe.ClaimManager;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.nation.tribe.TribeMember;
import com.sanan.avatarcore.util.nation.tribe.TribeRole;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("tribe|tr|t")
public final class TribeCommand extends BaseCommand implements Messageable {
    @Dependency
    private AvatarCore plugin;
    private final TribeManager tm = TribeManager.getInstance();
    private final PlayerManager pm = PlayerManager.getInstance();
    private final ClaimManager clm = ClaimManager.getInstance();

    @Default
    @CommandPermission("avatarcore.tribe.help")
    public void onDefault(Player player) {
        final List<String> helpMessage = this.plugin.getConfig().getStringList("messages.tribe-help");
        this.sendColoredList(player, helpMessage);
    }

    @Subcommand("help")
    @CommandPermission("avatarcore.tribe.help")
    public void help(Player player) {
        this.onDefault(player);
    }

    @Subcommand("create")
    @CommandPermission("avatarcore.tribe.create")
    @Syntax("[name]")
    public void create(Player player, @Single String name) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (this.tm.playerHasTribe(bendingPlayer)) {
            Message.ALREADY_IN_TRIBE.sendPlayer(player);
            return;
        }

        if (name.length() > 20) {
            Message.TRIBE_NAME_LONG.sendPlayer(player);
            return;
        }

        if (this.tm.isRegistered(name)) {
            Message.TRIBE_NAME_TAKEN.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = new BendingTribe(bendingPlayer, name);
        this.tm.registerTribe(tribe);
        this.sendColoredMessage(player, Message.TRIBE_CREATE.getMessage(), TextPlaceholder.of("%tribe%", name));
    }

    @Subcommand("show")
    @CommandPermission("avatarcore.tribe.show")
    @Syntax("[tribe name | member]")
    public void show(Player player, @Single String identifier) {
        BendingTribe result = null;
        if (!this.tm.isRegistered(identifier)) {
            final BendingTribe tribe = this.tm.lookupTribeByMember(identifier);
            if (tribe == null) {
                Message.TRIBE_NOT_EXIST.sendPlayer(player);
                return;
            }
            result = tribe;
        } else {
            result = this.tm.getTribe(identifier);
        }

        this.sendDetailedTribeInfoMessage(player, result);
    }

    @Subcommand("who")
    @CommandPermission("avatarcore.tribe.who")
    public void who(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        if (tribe == null) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        this.sendDetailedTribeInfoMessage(player, tribe);
    }

    private void sendDetailedTribeInfoMessage(Player player, BendingTribe tribe) {
        final List<String> onlinePlayers = new ArrayList<>(), offlinePlayers = new ArrayList<>(), allies = new ArrayList<>();
        for (BendingTribe ally : tribe.getAllies()) {
        	allies.add(ally.getName());
        }
        for (TribeMember member : tribe.getMembers()) {
            final StringBuilder memberName = new StringBuilder();
            if (member.getRole() != TribeRole.DEFAULT) memberName.append("*");
            if (member.getRole() != TribeRole.OFFICER) memberName.append("*");
            if (member.getRole() != TribeRole.CO_OWNER) memberName.append("*");
            memberName.append(member.getPlayerName());
            final UUID memberUuid = member.getUuid();
            if (Bukkit.getPlayer(memberUuid) == null) offlinePlayers.add(memberName.toString());
            else onlinePlayers.add(memberName.toString());
        }

        final List<String> message = this.plugin.getConfig().getStringList("messages.tribe-show");
        final TextPlaceholder power = TextPlaceholder.of("%tribepower%", Integer.toString(tribe.getTribePower()));
        final TextPlaceholder claimCount = TextPlaceholder.of("%claimcount%", Integer.toString(tribe.getClaimNumber()));
        final TextPlaceholder maxClaimCount = TextPlaceholder.of("%maxclaimcount%", Integer.toString(tribe.isTheLeaderRogue() ? 130 : 100));
        final TextPlaceholder onlinePlayerNames = TextPlaceholder.of("%online%", String.join(", ", onlinePlayers.toArray(new String[0])));
        final TextPlaceholder offlinePlayerNames = TextPlaceholder.of("%offline%", String.join(", ", offlinePlayers.toArray(new String[0])));
        final TextPlaceholder alliesName = TextPlaceholder.of("%allies%", String.join(", ", allies.toArray(new String[0])));
        final TextPlaceholder tribeName = TextPlaceholder.of("%tribe%", tribe.getName());
        final TextPlaceholder playerCount = TextPlaceholder.of("%playercount%", Integer.toString(tribe.getMembers().size()));
        final TextPlaceholder maxPlayerCount = TextPlaceholder.of("%maxplayercount%", Integer.toString(tribe.getMaxPlayer()));
        this.sendColoredList(player, message, power, claimCount, maxClaimCount, onlinePlayerNames, offlinePlayerNames, alliesName, tribeName, playerCount, maxPlayerCount);
    }

    @Subcommand("join")
    @CommandPermission("avatarcore.tribe.join")
    @Syntax("[tribe | member]")
    public void join(Player player, @Single String identifier) {
        final BendingPlayer bPlayer = this.pm.getBendingPlayer(player);
        if (this.tm.playerHasTribe(bPlayer)) {
            Message.ALREADY_IN_TRIBE.sendPlayer(player);
            return;
        }

        BendingTribe tribe = this.tm.lookupTribeByMember(identifier);
        if (tribe == null) {
            if (!this.tm.isRegistered(identifier)) {
                Message.TRIBE_NOT_EXIST.sendPlayer(player);
                return;
            }

            tribe = this.tm.getTribe(identifier);
        }

        if (tribe.getMembers().size() >= tribe.getMaxPlayer()) {
            Message.TRIBE_FULL.sendPlayer(player);
            return;
        }

        final TextPlaceholder tribePlaceholder = TextPlaceholder.of("%tribe%", tribe.getName());
        final TextPlaceholder targetPlaceholder = TextPlaceholder.of("%player%", player.getName());
        
        if (!bPlayer.hasJoinRequest(tribe)) {
        	if (tribe.hasJoinRequest(bPlayer)) {
        		player.sendMessage("You have already asked to join the " + tribe.getName() + " tribe.");
                return;
        	}
        	bPlayer.sendJoinRequest(tribe);
        	player.sendMessage("You have asked to join the " + tribe.getName() + " tribe.");
            tribe.sendTribeMessage( player.getName() + " sent a request to join your tribe.");
            return;
        }
        
        bPlayer.acceptJoinRequest(tribe);
        this.sendColoredMessage(player, Message.TRIBE_JOIN.getMessage(), tribePlaceholder);
        tribe.sendTribeMessage(this.coloredMessageWithReplacements(Message.TRIBE_JOIN_GLOBAL.getMessage(), targetPlaceholder));
    }

    @Subcommand("leave")
    @CommandPermission("avatarcore.tribe.leave")
    public void leave(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        if (tribe.getLeader().getUuid().equals(player.getUniqueId())) {
            Message.TRIBE_OWNER_LEAVE.sendPlayer(player);
            return;
        }

        tribe.removeMember(player.getUniqueId());
        this.sendColoredMessage(player, Message.TRIBE_LEAVE.getMessage(), TextPlaceholder.of("%tribe%", tribe.getName()));
        tribe.sendTribeMessage(this.coloredMessageWithReplacements(Message.TRIBE_LEAVE_GLOBAL.getMessage(), TextPlaceholder.of("%player%", player.getName())));
    }

    @Subcommand("home")
    @CommandPermission("avatarcore.tribe.home")
    public void home(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        if (!tribe.hasHomeLocation()) {
            Message.TRIBE_NO_HOME.sendPlayer(player);
            return;
        }
        
        Vector actualPlayerLocation = player.getLocation().toBlockLocation().toVector();
        Message.TELEPORTATION_COOLDOWN.sendPlayer(player);
		new BukkitRunnable() {
	        public void run() {
	        	if (!actualPlayerLocation.equals(player.getLocation().toBlockLocation().toVector())) {
	        		Message.TELEPORTATION_CANCELLED.sendPlayer(player);
	        		return;
	        	}
	        	player.teleportAsync(tribe.getHomeLocation());
	        }
	    }.runTaskLater(plugin, 60);
    }
    
    @Subcommand("sethome")
    @CommandPermission("avatarcore.tribe.sethome")
    public void sethome(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole == TribeRole.DEFAULT) {
        	Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
        	return;
        }
        
        Chunk chunk = player.getLocation().getChunk();
        for (Claim claim: tribe.getClaims()) {
        	if (claim.equals(chunk)) {
        		 Message.TRIBE_SETHOME.sendPlayer(player);
        	     tribe.setHomeLocation(player.getLocation());
        	     return;
        	}
        }
        Message.TRIBE_SETHOME_OUTSIDE.sendPlayer(player);
    }

    @Subcommand("promote")
    @CommandPermission("avatarcore.tribe.promote")
    @Syntax("[player]")
    public void promote(Player player, OnlinePlayer onlinePlayer) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.CO_OWNER && memberRole != TribeRole.LEADER) {
            Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
            return;
        }

        final Player targetBukkitPlayer = onlinePlayer.getPlayer();
        final BendingPlayer target = this.pm.getBendingPlayer(targetBukkitPlayer);
        if (!this.tm.playerHasTribe(target) || !this.tm.getTribe(target).equals(tribe)) {
            Message.TARGET_NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        if (!tribe.getMemberRole(targetBukkitPlayer.getUniqueId()).canBePromoted()) {
           	player.sendMessage("§cThis player is already the owner or a co-owner of this tribe!");
            return;
        }
        tribe.promote(targetBukkitPlayer.getUniqueId());

        tribe.sendTribeMessage("§a" + targetBukkitPlayer.getName() + " has been promoted to " + tribe.getMemberRole(targetBukkitPlayer.getUniqueId()).name() + ".");
    }
    
    @Subcommand("demote")
    @CommandPermission("avatarcore.tribe.demote")
    @Syntax("[player]")
    public void demote(Player player, OnlinePlayer onlinePlayer) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.CO_OWNER && memberRole != TribeRole.LEADER) {
            Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
            return;
        }

        final Player targetBukkitPlayer = onlinePlayer.getPlayer();
        final BendingPlayer target = this.pm.getBendingPlayer(targetBukkitPlayer);
        if (!this.tm.playerHasTribe(target) || !this.tm.getTribe(target).equals(tribe)) {
            Message.TARGET_NOT_IN_TRIBE.sendPlayer(player);
            return;
        }
        
        if (!tribe.getMemberRole(targetBukkitPlayer.getUniqueId()).canBeDemoted()) {
           	player.sendMessage("§cYou can't demote this player!");
            return;
        }
        
        tribe.demote(targetBukkitPlayer.getUniqueId());

        tribe.sendTribeMessage("§c" + targetBukkitPlayer.getName() + " has been demoted to " + tribe.getMemberRole(targetBukkitPlayer.getUniqueId()).name() + ".");
    }

    @Subcommand("invite")
    @CommandPermission("avatarcore.tribe.invite")
    @Syntax("[player]")
    public void invite(Player player, OnlinePlayer onlinePlayer) {
        final BendingPlayer bPlayer = this.pm.getBendingPlayer(player);
        if (!tm.playerHasTribe(bPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.CO_OWNER && memberRole != TribeRole.LEADER) {
            Message.TRIBE_OWNER_COMMAND.sendPlayer(player);
            return;
        }

        final Player targetBukkitPlayer = onlinePlayer.getPlayer();
        final BendingPlayer target = this.pm.getBendingPlayer(targetBukkitPlayer);

        if (tribe.getMembers().size() >= tribe.getMaxPlayer()) {
            Message.TRIBE_FULL.sendPlayer(player);
            return;
        }

        final TextPlaceholder tribePlaceholder = TextPlaceholder.of("%tribe%", tribe.getName());
        final TextPlaceholder targetPlaceholder = TextPlaceholder.of("%player%", targetBukkitPlayer.getName());

        if (tm.playerHasTribe(target) && tm.getTribe(target).equals(tribe)) {
        	player.sendMessage("This player already belongs to your tribe!");
           return;
        }
        
        if (target.hasJoinRequest(tribe)) {
        	player.sendMessage("You have already invited this player to your tribe!");
           return;
        }

        tribe.sendJoinRequest(target);
        this.sendColoredMessage(targetBukkitPlayer, Message.PLAYER_INVITED.getMessage(), tribePlaceholder);
        tribe.sendTribeMessage(this.coloredMessageWithReplacements(Message.PLAYER_INVITED_GLOBAL.getMessage(), targetPlaceholder));
    }
    
    @Subcommand("accept")
    @CommandPermission("avatarcore.tribe.invite")
    @Syntax("[player]")
    public void accepte(Player player, OnlinePlayer onlinePlayer) {
        final BendingPlayer bPlayer = this.pm.getBendingPlayer(player);
        if (!tm.playerHasTribe(bPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole == TribeRole.DEFAULT) {
            Message.TRIBE_OWNER_COMMAND.sendPlayer(player);
            return;
        }

        final Player targetBukkitPlayer = onlinePlayer.getPlayer();
        final BendingPlayer target = this.pm.getBendingPlayer(targetBukkitPlayer);

        if (tribe.getMembers().size() >= tribe.getMaxPlayer()) {
            Message.TRIBE_FULL.sendPlayer(player);
            return;
        }

        final TextPlaceholder tribePlaceholder = TextPlaceholder.of("%tribe%", tribe.getName());
        final TextPlaceholder targetPlaceholder = TextPlaceholder.of("%player%", targetBukkitPlayer.getName());

        if (tm.playerHasTribe(target)) {
        	player.sendMessage("This player is already in a tribe!");
        	return;
        }
        
        if (!tribe.hasJoinRequest(target)) {
        	player.sendMessage("This player did not ask to join your tribe!");
           return;
        }

        tribe.acceptJoinRequest(target);
        this.sendColoredMessage(targetBukkitPlayer, Message.TRIBE_JOIN.getMessage(), tribePlaceholder);
        tribe.sendTribeMessage(this.coloredMessageWithReplacements(Message.TRIBE_JOIN_GLOBAL.getMessage(), targetPlaceholder));
    }
    
    @Subcommand("ally")
    @CommandPermission("avatarcore.tribe.ally")
    @Syntax("[tribe]")
    public void ally(Player player, @Single String tribeName) {
        
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.CO_OWNER && memberRole != TribeRole.LEADER) {
            Message.TRIBE_OWNER_COMMAND.sendPlayer(player);
            return;
        }
        
    	if (!tm.isRegistered(tribeName)) {
    		player.sendMessage("This tribe does not exist!");
    		return;
    	}
    	
    	BendingTribe allyTribe = tm.getTribe(tribeName);
    	
    	if (tribe.equals(allyTribe)) {
    		player.sendMessage("You can't be allied with yourself!");
    		return;
    	}
    	
    	if (tribe.isAlly(allyTribe)) {
    		player.sendMessage("You are already an ally!");
    		return;
    	}
    	
    	if (tribe.hasAllyRequest(allyTribe)) {
    		tribe.createAlly(allyTribe);
    		tribe.sendTribeMessage("You are now allied with the tribe " + allyTribe.getName());
    		allyTribe.sendTribeMessage("You are now allied with the tribe " + tribe.getName());
    		return;
    	}
    	tribe.sendAllyRequest(allyTribe);
    	allyTribe.sendTribeMessage("You have received a request for ally. Press /t ally " + tribe.getName() + " to accept.");
    	player.sendMessage("You have sended a request for ally with " + allyTribe.getName() + ".");    	
    }
    
    @Subcommand("unally")
    @CommandPermission("avatarcore.tribe.ally")
    @Syntax("[tribe]")
    public void unally(Player player, @Single String tribeName) {
        
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.CO_OWNER && memberRole != TribeRole.LEADER) {
            Message.TRIBE_OWNER_COMMAND.sendPlayer(player);
            return;
        }
        
    	if (!tm.isRegistered(tribeName)) {
    		player.sendMessage("This tribe does not exist!");
    		return;
    	}
    	
    	BendingTribe allyTribe = tm.getTribe(tribeName);
    	
    	if (tribe.equals(allyTribe)) {
    		player.sendMessage("You can't be unallied with yourself!");
    		return;
    	}
    	
    	if (!tribe.isAlly(allyTribe)) {
    		player.sendMessage("You are not an ally!");
    		return;
    	}
    	
    	tribe.removeAlly(allyTribe);
    	allyTribe.removeAlly(tribe);
    	tribe.sendTribeMessage("You are no longer allied with the tribe " + allyTribe.getName());
    	allyTribe.sendTribeMessage("You are no longer allied with the tribe " + tribe.getName());    	
    }

    @Subcommand("kick")
    @CommandPermission("avatarcore.tribe.kick")
    @Syntax("[player]")
    public void kick(Player player, OnlinePlayer onlinePlayer) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole != TribeRole.LEADER && memberRole != TribeRole.CO_OWNER) {
            Message.TRIBE_OWNER_COMMAND.sendPlayer(player);
            return;
        }

        final Player targetBukkitPlayer = onlinePlayer.getPlayer();
        final BendingPlayer target = this.pm.getBendingPlayer(targetBukkitPlayer);
        if (!this.tm.playerHasTribe(target) || !this.tm.getTribe(target).equals(tribe)) {
            Message.TARGET_NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        if (tribe.getMemberRole(targetBukkitPlayer.getUniqueId()) != TribeRole.DEFAULT) {
            Message.TARGET_CANNOT_KICK.sendPlayer(player);
            return;
        }

        tribe.removeMember(targetBukkitPlayer.getUniqueId());

        final TextPlaceholder targetPlaceholder = TextPlaceholder.of("%player%", targetBukkitPlayer.getName());
        Message.TRIBE_KICKED.sendPlayer(targetBukkitPlayer);
        this.sendColoredMessage(player, Message.TRIBE_KICK.getMessage(), targetPlaceholder);
        tribe.sendTribeMessage(this.coloredMessageWithReplacements(Message.TRIBE_KICK_GLOBAL.getMessage(), targetPlaceholder));
    }

    @Subcommand("disband")
    @CommandPermission("avatarcore.tribe.disband")
    public void disband(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        if (tribe.getMemberRole(player.getUniqueId()) != TribeRole.LEADER) {
            //TODO: LEADER COMMAND
            return;
        }
        player.sendMessage("§4You will lose any current plot rights!");
        Message.TRIBE_DISBAND_CONFIRM.sendPlayer(player);
        TribeDisbandListener.addDisbanding(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                TribeDisbandListener.removeDisbanding(player);
            }
        }.runTaskLater(this.plugin, 10L * 20L);

    }
    
    @Subcommand("map")
    @CommandPermission("avatarcore.tribe.map")
    public void map(Player player) {
    	if (!player.getWorld().equals(Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get()))) {
    		player.sendMessage("You can't use the map in this world!");
    		return;
    	}
        player.sendMessage(ClaimManager.getInstance().getMap(player));

    }
    
    @Subcommand("claim")
    @CommandPermission("avatarcore.tribe.claim")
    public void claim(Player player, @Optional Integer radius) {
    	if (!player.getWorld().equals(Bukkit.getWorld((@NotNull String) Setting.MAIN_WORLD_NAME.get()))) {
    		player.sendMessage("You can't claim in this world!");
    		return;
    	}
    	
    	RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
    	Location loc = player.getLocation();
        ApplicableRegionSet ars = regions.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
        if (ars.getRegions().size() != 0) {
        	player.sendMessage("You can't claim here!");
        	return;
        }
    	
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole == TribeRole.DEFAULT) {
        	Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
        	return;
        }
        
        if (tribe.getClaimNumber() >= tribe.getMaxClaims() && tribe.getClaimNumber() < (tribe.isTheLeaderRogue() ? 130 : 100)) {
        	player.sendMessage("§cYou need more power to claim this land!  Invite players to gain additional power.");
        	return;
        }
        
        if (tribe.getClaimNumber() >= tribe.getMaxClaims()) {
        	player.sendMessage("§cYou have reached the maximum number of claims!");
        	return;
        }
        
		if (radius == null) {
	        Claim claim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
	        BendingTribe claimOwner = clm.getClaims().get(claim);
	        
			if (claimOwner != null && claimOwner.getTribePower() >= claimOwner.getClaimNumber()) {
				player.sendMessage("This land is already owned by " + claimOwner.getName() + ".");
				return;
			}
			
			if (claimOwner != null && claimOwner.equals(tribe)) {
				player.sendMessage("You already have this claim!");
				return;
			}
		
			if (claimOwner != null) {
				tribe.sendTribeMessage(player.getName() + " has claimed 1 chunk of land from " + claimOwner.getName() + "'s tribe.");
				claimOwner.unclaim(claim);
			}
			else {
				tribe.sendTribeMessage(player.getName() + " has claimed 1 chunk of land");
			}
	        tribe.claim(claim);     
		}
		else {
			int claimNumber = tribe.getClaimNumber();
			Claim actualClaim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
			tribe.tryClaim(actualClaim);
			for (int i = 1; i <= radius; i++) {
				actualClaim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
				actualClaim = new Claim(actualClaim.getX(), actualClaim.getY() - i, player.getWorld());
				tribe.tryClaim(actualClaim);
				for (int j = 0; j < i; j++) {
					actualClaim = new Claim(actualClaim.getX() + 1, actualClaim.getY(), player.getWorld());
					tribe.tryClaim(actualClaim);
				}
				for (int j = 0; j < 2*i; j++) {
					actualClaim = new Claim(actualClaim.getX(), actualClaim.getY() + 1, player.getWorld());
					tribe.tryClaim(actualClaim);
				}
				for (int j = 0; j < 2*i; j++) {
					actualClaim = new Claim(actualClaim.getX() - 1, actualClaim.getY(), player.getWorld());
					tribe.tryClaim(actualClaim);
				}
				for (int j = 0; j < 2*i; j++) {
					actualClaim = new Claim(actualClaim.getX(), actualClaim.getY() - 1, player.getWorld());
					tribe.tryClaim(actualClaim);
				}
				for (int j = 0; j < i - 1; j++) {
					actualClaim = new Claim(actualClaim.getX() + 1, actualClaim.getY(), player.getWorld());
					tribe.tryClaim(actualClaim);
				}
			}
			if (claimNumber != tribe.getClaimNumber()) {
				player.sendMessage("Vous have claim " + (tribe.getClaimNumber() - claimNumber) + " lands.");
			}
			else {
				player.sendMessage("You have no claim to any land.");
			}
		}
    }
    
    @Subcommand("unclaim")
    @CommandPermission("avatarcore.tribe.unclaim")
    public void unclaim(Player player, @Optional String isAll) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole == TribeRole.DEFAULT) {
        	Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
        	return;
        }
        
        if (isAll == null) {
	        Claim claim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
	        BendingTribe claimOwner = clm.getClaims().get(claim);
	        if (claimOwner == null || !claimOwner.equals(tribe)) {
	        	player.sendMessage("This claim does not belong to you");
	        	return;
	        }
	        tribe.unclaim(claim);
	        tribe.sendTribeMessage(player.getName() + " has unclaimed 1 chunk of land");
        }
        else if (isAll.equalsIgnoreCase("all")) {
        	tribe.unclaimAll();
        	tribe.sendTribeMessage(player.getName() + " has unclaimed all lands");
        }
      
    }
    
    @Subcommand("autoclaim")
    @CommandPermission("avatarcore.tribe.autoclaim")
    public void autoclaim(Player player) {
        final BendingPlayer bendingPlayer = this.pm.getBendingPlayer(player);
        if (!this.tm.playerHasTribe(bendingPlayer)) {
            Message.NOT_IN_TRIBE.sendPlayer(player);
            return;
        }

        final BendingTribe tribe = this.tm.getTribe(bendingPlayer);
        final TribeRole memberRole = tribe.getMemberRole(player.getUniqueId());
        if (memberRole == TribeRole.DEFAULT) {
        	Message.TRIBE_PERMISSION_COMMAND.sendPlayer(player);
        	return;
        }
        TribeMember tPlayer = tribe.getMember(player.getUniqueId());
        if (tPlayer.isInAutoClaim()) {
        	tPlayer.activeAutoClaim(false);
        	player.sendMessage("Autoclaim deactivated.");
        }
        else {
        	tPlayer.activeAutoClaim(true);
        	player.sendMessage("Autoclaim activated.");
        }
       
      
    }
}
