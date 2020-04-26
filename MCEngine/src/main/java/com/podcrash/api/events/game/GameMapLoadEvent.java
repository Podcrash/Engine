package com.podcrash.api.events.game;

import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.game.Game;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

/**
 * This will be called when the game is started
 */
public class GameMapLoadEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final GameMap map;
    private final World world;

    public GameMapLoadEvent(Game game, GameMap map, World world) {
        super(game, "loading the map " + map.getName());
        this.map = map;
        this.world = world;
    }

    public GameMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
