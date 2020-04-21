package com.podcrash.api.mc.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utils for creating item stacks.
 */

public class ItemStackUtil {

    public static ItemStack createItem(Material material, String displayName, List<String> lore) {
        return createItem(material, 1, displayName, lore);
    }
    /**
     * Creates an item with the following data
     * @param material The material.
     * @param amount The amount of items in the stack.
     * @param displayName The display name of the item.
     * @param lore The lore strings of the item.
     * @return The final itemstack.
     */
    public static ItemStack createItem(Material material, int amount, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an item stack.
     *
     * @param materialId The material ID.
     * @param amount The amount of items in the stack.
     * @param displayName The display name of the item.
     * @param loreString The lore strings of the item.
     * @return The final itemstack.
     */
    @SuppressWarnings("deprecation")
    public static ItemStack createItem(int materialId, int amount, String displayName, String... loreString) {
        ItemStack item;
        List<String> lore = new ArrayList<String>();

        item = new ItemStack(Material.getMaterial(materialId), amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtil.chat(displayName));

        String[] list = loreString;
        for (String s : list) {
            lore.add(ChatUtil.chat(s));
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Create an item stack with a byte ID.
     *
     * @param materialId The material ID.
     * @param byteId The byte ID.
     * @param amount The amount of items in the stack.
     * @param displayName The display name of the item.
     * @param loreString The lore strings of the item.
     * @return The final itemstack.
     */
    @SuppressWarnings("deprecation")
    public static ItemStack createItem(int materialId, int byteId, int amount, String displayName, String... loreString) {
        ItemStack item;
        List<String> lore = new ArrayList<String>();

        item = new ItemStack(Material.getMaterial(materialId), amount, (short) byteId);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtil.chat(displayName));

        String[] list = loreString;
        for (String s : list) {
            lore.add(ChatUtil.chat(s));
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an item and place it in an inventory slot.
     *
     * @param inv The inventory.
     * @param materialId The material ID.
     * @param amount The amount of items in the stack.
     * @param invSlot The inventory slot.
     * @param displayName The display name of the item.
     * @param loreString The lore strings of the item
     */
    public static void createItem(Inventory inv, int materialId, int amount, int invSlot, String displayName, String... loreString) {
        ItemStack item = createItem(materialId, amount, displayName, loreString);
        inv.setItem(invSlot - 1, item);
    }

    /**
     * Create an item with a byte id and place it in an inventory slot.
     *
     * @param inv The inventory.
     * @param materialId The material ID.
     * @param byteId The byte ID.
     * @param amount The amount of items in the stack.
     * @param invSlot The inventory slot.
     * @param displayName The display name of the item.
     * @param loreString The lore strings of the item
     * @return
     */
    public static void createItem(Inventory inv, int materialId, int byteId, int amount, int invSlot, String displayName, String... loreString) {
        ItemStack item = createItem(materialId, byteId, amount, displayName, loreString);
        inv.setItem(invSlot - 1, item);
    }
}
