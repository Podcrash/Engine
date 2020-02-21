package com.podcrash.api.mc.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class CustomInventoryListener extends ListenerBase {
    public CustomInventoryListener (JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Material item = event.getMaterial();
        Player player = event.getPlayer();

        if (item == Material.COMPASS) {
            //TODO add support for opening hub Inventory
        }

    }

}
