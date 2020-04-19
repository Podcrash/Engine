package com.podcrash.api.mc.util;

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
}
