package com.podcrash.api.mc.ui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CreateInventory {

    public static Map<String, Inventory> inventoryMap = new HashMap<>();

    public static Inventory createCustomInventory(String name, int size, Map<Integer, ItemStack> items) {
        Inventory newInventory = Bukkit.createInventory(null, size, name);

        for (Map.Entry<Integer, ItemStack> item : items.entrySet()) {
            int itemPos = item.getKey();
            ItemStack itemStack = item.getValue();

            newInventory.setItem(itemPos, itemStack);
        }

        inventoryMap.put(name, newInventory);
        return newInventory;

    }

    public static Inventory getInventoryByName(String name) {
        Inventory inventory = inventoryMap.get(name);

        return inventory;
    }

}
