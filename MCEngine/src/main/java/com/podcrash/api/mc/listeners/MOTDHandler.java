package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MOTDHandler extends ListenerBase {
    public MOTDHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        Game game = GameManager.getGame();
        if (game == null)
            return;
        e.setMotd(game.getGameState().toString());
    }
}
