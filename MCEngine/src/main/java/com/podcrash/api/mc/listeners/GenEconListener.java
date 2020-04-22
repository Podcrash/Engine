package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.events.econ.PayEvent;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class GenEconListener extends ListenerBase {
    public GenEconListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void pay(PayEvent e) {
        if (PodcrashSpigot.getInstance().hasPPLOwner())
            e.setCancelled(true);
    }
}
