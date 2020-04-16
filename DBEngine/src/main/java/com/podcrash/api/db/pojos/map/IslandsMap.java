package com.podcrash.api.db.pojos.map;

public class IslandsMap extends GameMap {

    private int waterDmg;

    public IslandsMap(int waterDmg) {
        super();
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
