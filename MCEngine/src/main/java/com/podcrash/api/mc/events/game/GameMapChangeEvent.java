package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.event.HandlerList;

public class GameMapChangeEvent extends GameEvent {
    private final String map;
    private static final HandlerList handlers = new HandlerList();
    public GameMapChangeEvent(Game game, String map) {
        super(game, "Map changed");
        this.map = map;
    }

    public String getMap() {
        return map;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
