package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.callback.helpers.TrapSetter;
import com.podcrash.api.mc.events.TrapSnareEvent;
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
        if(!TrapSetter.deleteTrap(item)) return;
        e.setCancelled(true);
        Bukkit.getPluginManager().callEvent(new TrapSnareEvent(e.getItem(), e.getPlayer()));
    }
}
