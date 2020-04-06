package com.podcrash.api.db.pojos.map;

import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

public class IslandsMap extends GameMap {

    private int waterDmg;
    List<Location> bridgePoints;

    public IslandsMap() {
        super();
        this.bridgePoints = new LinkedList<>();
    }

    public void addBridgePoint(Location location) {
        this.bridgePoints.add(location);
    }

    public void removeBridgePoint(Location location) {
        this.bridgePoints.remove(location);
    }

    public List<Location> getBridgePoints() {
        return bridgePoints;
    }


    public void setWaterDmg(int waterDmg) {
        this.waterDmg = waterDmg;
    }

    public int getWaterDmg() {
        return waterDmg;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("IslandsMap{");
        sb.append("launchPads=").append(getLaunchPads());
        sb.append(", teleportPads=").append(getTeleportPads());
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", gamemode='").append(getGamemode()).append('\'');
        sb.append(", authors=").append(getAuthors());
        sb.append(", spawns=").append(getSpawns());
        sb.append(", defaultSpawn=").append(getDefaultSpawn());
        sb.append(", environment='").append(getEnvironment()).append('\'');
        sb.append(", worldType='").append(getWorldType()).append('\'');
        sb.append(", allowAnimals=").append(isAllowAnimals());
        sb.append(", allowPvP=").append(isAllowPvP());
        sb.append(", allowMonsters=").append(isAllowMonsters());
        sb.append('}');
        return sb.toString();
    }

}
