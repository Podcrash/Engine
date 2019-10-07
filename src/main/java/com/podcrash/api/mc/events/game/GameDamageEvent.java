package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameDamageEvent extends GamePlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player victim;

    public GameDamageEvent(Game game, Player who, Player victim) {
        super(game, who, "");
        this.victim = victim;
    }

    public GameDamageEvent(Game game, Player who, String message) {
        super(game, who, message);
    }

    public Player getVictim() {
        return victim;
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
