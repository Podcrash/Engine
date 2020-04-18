package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private GameState state;

    public GameStateEvent(Game game, GameState state) {
        this.game = game;
        this.state = state;
    }

    public Game getGame() {
        return game;
    }

    public GameState getState() {
        return state;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
