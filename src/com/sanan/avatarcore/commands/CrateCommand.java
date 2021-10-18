package com.sanan.avatarcore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.crate.CrateInventoryUtil;
import com.sanan.avatarcore.util.crate.CrateItemUtil;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("crate")
public class CrateCommand extends BaseCommand implements Messageable {
	
    @Dependency
    private AvatarCore plugin;
    
	@Default
    @CommandPermission("avatarcore.crate")
    @CommandCompletion("Hero|Champion|Master|Lord|Legend|WhiteLotus")
    public void onDefault(CommandSender sender, @Single String type) {
		if (sender instanceof Player) {
			((Player) sender).openInventory(CrateInventoryUtil.getCrateCreateListMenu(type));
		}
    }
	
	@Subcommand("collect")
	@CommandPermission("avatarcore.crate.collect")
    @CommandCompletion("Hero|Champion|Master|Lord|Legend|WhiteLotus")
    public void sell(Player player, @Single String type) {
		player.getInventory().addItem(CrateItemUtil.getCrateShard(type));
	}
}
