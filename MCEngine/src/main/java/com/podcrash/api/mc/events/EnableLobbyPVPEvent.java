package com.podcrash.api.mc.events;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is called when a player successfully opts into lobby PVP.
 *
 * This event is called so the games can individually handle the inventory setup, which will
 * most likely differ between every game (e.g. Conquest and Squad Assault)
 *
 *
 */
public class EnableLobbyPVPEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String mode;

    public EnableLobbyPVPEvent (Player player, String mode) {
        this.player = player;
        this.mode = mode;
    }

    public Player getPlayer() {return player;}

    public String getGameType() {return mode;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
