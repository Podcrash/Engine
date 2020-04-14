package com.podcrash.api.db.pojos.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap extends BaseMap {
    /**
     * for ex:
     * @see org.bukkit.DyeColor#BLACK
     * the "BLACK" part will serve as the key
     */
    private Map<String, List<Point>> spawns;
    public GameMap() {
        this.spawns = new HashMap<>();
    }


    public Map<String, List<Point>> getSpawns() {
        return spawns;
    }

    public void setSpawns(Map<String, List<Point>> spawns) {
        this.spawns = spawns;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameMap{");
        sb.append("gamemode='").append(getGamemode()).append('\'');
        sb.append(", spawns=").append(spawns);
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", authors=").append(getAuthors());
        sb.append(", defaultSpawn=").append(getDefaultSpawn());
        sb.append(", environment='").append(getEnvironment()).append('\'');
        sb.append(", worldType='").append(getWorldType()).append('\'');
        sb.append(", allowAnimals=").append(isAllowAnimals());
        sb.append(", allowPvP=").append(isAllowPvP());
        sb.append(", allowMonsters=").append(isAllowMonsters());
        sb.append(", launchPads=").append(getLaunchPads());
        sb.append(", teleportPads=").append(getTeleportPads());
        sb.append('}');
        return sb.toString();
    }
}
