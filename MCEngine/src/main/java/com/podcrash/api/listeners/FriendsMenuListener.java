package com.podcrash.api.listeners;

import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class FriendsMenuListener extends ListenerBase {
    public FriendsMenuListener(JavaPlugin plugin) {super(plugin); }

    public static Inventory createFriendsMenu() {
        Inventory inventory = Bukkit.createInventory(null, 45, "Friends");
        ItemStackUtil.createItem(inventory, 152, 1, 15, "Test!");

        return inventory;
    }
}
