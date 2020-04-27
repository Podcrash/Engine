package com.podcrash.api.util;

import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static void clearSaveHotbar(Inventory inv) {
        ItemStack[] hotbarSave = inv.getContents();
        inv.clear();
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, hotbarSave[i]);
        }
    }

    /**
     * Clear the player's inventory
     * @param player
     */
    public static void clearHotbarSelection(Player player) {
        Inventory inv = player.getInventory();
        for(int i = 9; i < 36; i++) {
            inv.setItem(i, new ItemStack(Material.AIR, 1));
        }
        Bukkit.getScheduler().runTaskLater(PodcrashSpigot.getInstance(), player::updateInventory, 1L);
    }
}
