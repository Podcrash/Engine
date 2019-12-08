package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.map.BaseGameMap;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldLoadEvent;

public class GameMapLoadEvent extends GameEvent {
    private static HandlerList handlers = new HandlerList();
    private BaseGameMap map;
    private World world;

    public GameMapLoadEvent(Game game, BaseGameMap map, World world) {
        super(game, "loading the map " + map.getName());
        this.map = map;
        this.world = world;
    }

    public BaseGameMap getMap() {
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
