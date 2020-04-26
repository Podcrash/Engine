package com.podcrash.api.events.game;

import com.podcrash.api.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();
    public Location spawnlocation;

    public GameEndEvent(Game game, Location spawnlocation) {
        super(game, ChatColor.BOLD + "\nGame " + game.getId() + " has ended.\n" + "");
//                "Red Team: " + game.getRedScore() + "\n" +
//                "Blue Team: " + game.getBlueScore());
        this.spawnlocation = spawnlocation;
    }

    public GameEndEvent(Game game, String message, Location spawnlocation) {
        super(game, message);
        this.spawnlocation = spawnlocation;
    }

    public Location getSpawnlocation() {
        return spawnlocation;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

