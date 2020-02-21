package com.podcrash.api.mc.ui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CreateCustomInventory {
    public static Inventory createInventory(String name, int size, Map<Integer, ItemStack> items) {
        Inventory newInventory = Bukkit.createInventory(null, size, name);

        for (Map.Entry<Integer, ItemStack> item: items.entrySet()) {
            int itemPos = item.getKey();
            ItemStack itemStack = item.getValue();

            newInventory.setItem(itemPos, itemStack);
        }

        return newInventory;
    }
}
