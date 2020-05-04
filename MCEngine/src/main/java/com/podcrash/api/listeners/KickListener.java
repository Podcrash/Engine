package com.podcrash.api.listeners;

import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.util.BungeeUtil;
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
        String reason = e.getReason();
        e.getPlayer().sendMessage("You have been kicked from " + Communicator.getCode() + ": " + reason);
        BungeeUtil.sendToServer(e.getPlayer(), "hub");
    }
}
