package com.podcrash.api.listeners;

import com.podcrash.api.events.DropDeathLootEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathHandler extends ListenerBase {
    private static boolean allowPlayerDrops = false;
    public DeathHandler(JavaPlugin plugin) {
        super(plugin);
    }

    public static boolean isAllowPlayerDrops() {
        return allowPlayerDrops;
    }

    public static void setAllowPlayerDrops(boolean allowPlayerDrops) {
        DeathHandler.allowPlayerDrops = allowPlayerDrops;
    }

    @EventHandler
    public void die(DropDeathLootEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            if(!allowPlayerDrops)
                event.setCancelled(true);
        }
    }
}
