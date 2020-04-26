package com.podcrash.api.listeners;

import com.podcrash.api.callback.helpers.TrapSetter;
import com.podcrash.api.events.TrapSnareEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TrapListener extends ListenerBase {
    public TrapListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void itemPickUp(PlayerPickupItemEvent e) {
        Item item = e.getItem();
        if (!TrapSetter.deleteTrap(item)) return;
        TrapSnareEvent snareEvent = new TrapSnareEvent(e.getItem(), e.getPlayer());
        Bukkit.getPluginManager().callEvent(snareEvent);
        if (!snareEvent.isCancelled()) {
            e.setCancelled(true);
            item.remove();
        }
    }
}
