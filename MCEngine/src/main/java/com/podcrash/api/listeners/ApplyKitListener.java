package com.podcrash.api.listeners;

import com.podcrash.api.events.skill.ApplyKitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ApplyKitListener extends ListenerBase {
    public ApplyKitListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void kit(ApplyKitEvent e) {
        if(e.getKitPlayer().getPlayer().getWorld().getName().equals("world")) {
            e.setKeepInventory(true);
        }
    }
}
