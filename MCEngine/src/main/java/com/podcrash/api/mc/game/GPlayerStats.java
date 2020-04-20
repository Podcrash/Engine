package com.podcrash.api.mc.game;

import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

/**
 * A Player's Game Stats
 *
 * @author JJCunningCreeper
 */

public abstract class GPlayerStats {

    private final UUID uuid;
    private final String name;

    /**
     * Constructor for PlayerStats.
     * @param uuid
     */
    public GPlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getPlayer(uuid).getName();
    }

    /**
     * @return The player's UUID.
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @return The player's username.
     */
    public String getPlayerName() {
        return name;
    }

    /**
     * A list of strings that will be used to display the player's stats.
     * Performance summary at the end of the game, hovertext in chat during the game, etc.
     * @return A list of strings that display the player's stats for the game (with chat formatting).
     */
    public abstract List<String> getStats();

}
