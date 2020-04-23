package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.util.BungeeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KickListener extends ListenerBase {
    public KickListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void kick(PlayerKickEvent e) {
        e.setCancelled(true);
        //maybe we'll process reasons later
        BungeeUtil.sendToServer(e.getPlayer(), "hub");
    }
}
