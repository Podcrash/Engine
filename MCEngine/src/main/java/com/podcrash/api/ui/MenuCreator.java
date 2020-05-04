package com.podcrash.api.ui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Class for simple Inventory Menu GUIs.
 *
 * @author JJCunningCreeper
 */
public class MenuCreator {

    /**
     * Create a simple menu inventory with all items listed without gaps.
     * @param name The name of the inventory.
     * @param heading The heading item of the inventory. Null if none.
     * @param items The list of items for the menu.
     * @param maxSlots If the menu should have the complete double-chest slots (54 slots).
     * @param border If there is a margin/border.
     * @return The inventory menu.
     */
    public static Inventory createMenuSimple(String name, ItemStack heading, List<ItemStack> items, boolean maxSlots, boolean border) {
        // Determine the rows and size values.
        int rows = 0;
        if (maxSlots) {
            rows = 6;
        } else {
            int row_count = 9;
            if (border) { row_count = 7; }
            rows = items.size() / row_count;
            if (items.size() % row_count != 0) { rows++; }
            if (heading != null) { rows++; }
            if (border) { rows++; }
            if (rows > 6) { rows = 6; }
        }
        int size = rows * 9;
        // Create the menu.
        Inventory result = Bukkit.createInventory(null, size, name);
        // Populate the menu.
        int i = 0;
        int finalSlot = size;
        if (heading != null || border) { finalSlot = finalSlot - 9; }
        if (heading != null) {
            result.setItem(4, heading);
            i = 9;
        }
        for (ItemStack item : items) {
            if (i < finalSlot) {
                if (border)
                    while (i % 9 == 0 || (i + 1) % 9 == 0) {
                        i++;
                    }
                result.setItem(i, item);
            }
            i++;
        }
        return result;
    }

    /**
     * Create a menu with all the items listed with gaps and centered.
     * @param name The name of the inventory.
     * @param heading The heading item of the inventory. Null if none.
     * @param items The list of items for the menu.
     * @param maxSlots If the menu should have the complete double-chest slots (54 slots).
     * @return The inventory name.
     */
    public static Inventory createMenu(String name, ItemStack heading, List<ItemStack> items, boolean maxSlots) {
        // Determine the rows and size values.
        int rows = 0;
        if (maxSlots) {
            rows = 6;
        } else {
            rows = items.size() / 5;
            if (items.size() % 5 != 0)
                rows = rows + 1;
            if (heading != null)
                rows++;
            if (rows > 6)
                rows = 6;
        }
        int size = rows * 9;
        // Create the menu.
        Inventory result = Bukkit.createInventory(null, size, name);
        // Populate the menu.
        int i = 0;
        int finalSlot = size;
        int itemRows = rows;
        if (heading != null) {
            result.setItem(4, heading);
            i = 9;
            finalSlot = finalSlot - 9;
            itemRows = itemRows - 2;
        }
        int threshold = items.size()/itemRows;
        if (items.size() % itemRows != 0) { threshold++; }
        int start = 0;
        int end = threshold;
        i = i + 4;
        while (start < items.size()) {
            List<ItemStack> tmp = items.subList(start, end);
            int num = tmp.size();
            int j = i + 1 - num;
            for (ItemStack item : tmp) {
                result.setItem(j, item);
                if (j < finalSlot)
                    j = j + 2;
            }
            start = start + threshold;
            end = end + threshold;
            if (end > items.size()) { end = items.size(); }
            i = i + 9;
        }
        return result;
    }
}
