package com.sanan.avatarcore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagInt;
import net.minecraft.server.v1_16_R3.NBTTagIntArray;
import net.minecraft.server.v1_16_R3.NBTTagShort;
import net.minecraft.server.v1_16_R3.NBTTagString;

@CommandAlias("bending|bend")
public class LevelCommand extends BaseCommand implements Messageable {
    @Dependency
    private AvatarCore plugin;
    private final PlayerManager pm = PlayerManager.getInstance();
    
	@Default
    @CommandPermission("avatarcore.bending.info")
    public void onDefault(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(pm.getBendingPlayer((Player) sender).getBendingLevel().getBendingInfo());
		}
    }
	
	@Subcommand("join")
    @CommandPermission("avatarcore.bending.join")
    @CommandCompletion("fire|earth|air|water")
    @Syntax("[bending class]")
    public void join(Player player, @Single String bendingClass) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		for (BendingElement element : BendingElement.getBendingElements()) {
			if (element.getName().toLowerCase().contains((bendingClass))) {
				player.sendMessage(bPlayer.getBendingLevel().setClass(bendingClass));
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set essentials.warps.upgrade false");
			}
		}
	}
	
	@Subcommand("boost")
    @CommandPermission("avatarcore.bending.boost")
    @Syntax("[xp amount]")
    public void boost(Player player, @Single int xp) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		bPlayer.addXp(xp);
	}
	
	@Subcommand("display")
    @CommandPermission("avatarcore.bending.display")
    public void display(Player player) {
		ArrayList<String> bendingInfo = new ArrayList<String>();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		for (HashMap.Entry<String, Integer> bendingClass : bPlayer.getBendingLevel().getBendingLevel().entrySet()) {
			if (bendingClass.getValue() > 0) {
				bendingInfo.add(bPlayer.getBendingLevel().getBendingInfo(bendingClass.getKey()));
			}
		}
		if (bendingInfo.size() == 0) { bendingInfo.add("You do not have a class selected."); }
		player.sendMessage(bendingInfo.toArray(new String[0]));
	}
	
	@Subcommand("withdraw")
    @CommandPermission("avatarcore.bending.withdraw")
    @Syntax("[xp amount]")
    public void withdraw(Player player, @Single int xp) {
		ItemStack item = new ItemBuilder(new ItemStack(Material.NETHER_STAR))
				.setName("§6§lBending Boost")
				.addLore("§6§lBending XP", "", "§c[" + xp + "]", "§bRight Click to Redeem!")
				.setCustomItemData(new Random().nextInt())				
				.build();
		pm.give(player, Arrays.asList(item));
	}

}
