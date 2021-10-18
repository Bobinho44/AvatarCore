package com.sanan.avatarcore.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface Messageable {
    default void sendColoredMessage(CommandSender sender, String text, TextPlaceholder... placeholders) {
        this.sendColoredMessage(sender, this.messageWithReplacements(text, placeholders));
    }

    default void sendColoredMessage(CommandSender sender, String text) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }

    default void sendColoredList(CommandSender sender, List<String> array) {
        if (array.isEmpty()) return;
        array.forEach(message -> sendColoredMessage(sender, message));
    }

    default void sendColoredList(CommandSender sender, List<String> array, TextPlaceholder... placeholders) {
        this.sendColoredList(sender, this.messagesWithReplacements(array, placeholders));
    }

    default String coloredMessageWithReplacements(String text, TextPlaceholder... placeholders) {
        return this.messageWithReplacements(ChatColor.translateAlternateColorCodes('&', text), placeholders);
    }

    default String messageWithReplacements(String text, TextPlaceholder... placeholders) {
        for (TextPlaceholder placeholder : placeholders) {
            text = StringUtils.replace(text, placeholder.getOldValue(), placeholder.getReplacement());
        }
        return text;
    }

    default List<String> messagesWithReplacements(List<String> array, TextPlaceholder... placeholders) {
        final List<String> result = new ArrayList<>();
        for (String message : array) {
            String updated = new String(message);
            for (TextPlaceholder placeholder : placeholders) {
                updated = StringUtils.replace(updated, placeholder.getOldValue(), placeholder.getReplacement());
            }
            result.add(ChatColor.translateAlternateColorCodes('&', updated));
        }
        return result;
    }
}
