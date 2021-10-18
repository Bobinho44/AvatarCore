package com.sanan.avatarcore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;

@CommandAlias("skip")
public class TutorialCommand extends BaseCommand implements Messageable {
	
    @Dependency
    private AvatarCore plugin;
    private final PlayerManager pm = PlayerManager.getInstance();
    
	@Default
    @CommandPermission("avatarcore.tutorial")
    public void onDefault(CommandSender sender) {
		if (sender instanceof Player) {
			BendingPlayer bPlayer = pm.getBendingPlayer((Player) sender);
			if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() > 0 && ((Player) sender).getWorld().getName().equalsIgnoreCase("tutorial")) {
				bPlayer.skipTutorial();
			}
			else {
				sender.sendMessage("§cYou have not started the tutorial!");
			}
		}
    }
}
