package com.sanan.avatarcore.util.item;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.base.Preconditions;
import com.sanan.avatarcore.AvatarCore;

public class ItemBuilder {
	
	private final AvatarCore ac = AvatarCore.getInstance();
	
    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder hideEnchants() {
    	ItemMeta meta = itemStack.getItemMeta();
    	meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    	itemStack.setItemMeta(meta);
    	return this;
    }
    
    
    public ItemBuilder setDurability(short dur) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        Preconditions.checkState(itemMeta instanceof Damageable, "meta is not an instance of Damageable");
        ((Damageable) itemMeta).setDamage(dur);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setName(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        itemStack.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        itemStack.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchants(HashMap<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
            addEnchant(enchant.getKey(), enchant.getValue());
        }

        return this;
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;

            bookMeta.addStoredEnchant(enchantment, level, true);
            itemStack.setItemMeta(bookMeta);
        }

        return this;
    }

    @SuppressWarnings("deprecation")
	public ItemBuilder setLore(String... lore) {
        Preconditions.checkNotNull(lore, "lore cannot be null");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    @SuppressWarnings("deprecation")
	public ItemBuilder addLore(String... lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> current = itemMeta.getLore();
        if (current == null) {
            current = new ArrayList<>();
        }

        current.addAll(Arrays.asList(lore));
        itemMeta.setLore(current);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) itemMeta;
            im.setColor(color);
            itemStack.setItemMeta(im);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setBreakable(boolean breakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(breakable);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    
    public ItemBuilder setCustomItemData(int data) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(data);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    
    public ItemStack build() {
        return itemStack;
    }
}