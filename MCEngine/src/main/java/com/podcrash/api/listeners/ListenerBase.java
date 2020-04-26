package com.podcrash.api.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerBase implements Listener {

    public ListenerBase(JavaPlugin plugin) {
        plugin.getLogger().info(String.format("[LISTENER] Loading in %s", this.getClass().getSimpleName()));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
