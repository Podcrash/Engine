package com.podcrash.api.events.game;

import com.podcrash.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameDamageEvent extends GamePlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player killer;

    public GameDamageEvent(Game game, Player who, Player killer) {
        super(game, who, "", true);
        this.killer = killer;
    }

    public GameDamageEvent(Game game, Player who, String message) {
        super(game, who, message);
    }

    public Player getKiller() {
        return killer;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
