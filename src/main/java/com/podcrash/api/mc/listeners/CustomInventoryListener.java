package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.ui.CreateInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomInventoryListener extends ListenerBase {

    public CustomInventoryListener (JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Inventory hubInventory = CreateInventory.getInventoryByName("hubInventory");

        //TODO: Add support for opening the inventory based on item clicks

    }

}
