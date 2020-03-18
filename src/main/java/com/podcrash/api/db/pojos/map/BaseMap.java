package com.podcrash.api.db.pojos.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseMap {
    private String name;
    private List<String> authors;
    /**
     * for ex:
     * @see org.bukkit.DyeColor#BLACK
     * the "BLACK" part will serve as the key
     */
    private Map<String, List<Point>> spawns;

    //IE: islands, conquest, squadassault, etc
    private String gamemode;

    private Point defaultSpawn;
    private String environment;
    private String worldType;
    private boolean allowAnimals;
    private boolean allowPvP;
    private boolean allowMonsters;

    private List<Point2Point> launchPads;
    private List<Point2Point> teleportPads;

    public BaseMap() {
        this.authors = new ArrayList<>();
        this.spawns = new HashMap<>();
        this.allowAnimals = true;
        this.allowPvP = true;
        this.allowMonsters = true;
        this.environment = "normal";
        this.worldType = "default";

        this.launchPads = new ArrayList<>();
        this.teleportPads = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Map<String, List<Point>> getSpawns() {
        return spawns;
    }

    public void setSpawns(Map<String, List<Point>> spawns) {
        this.spawns = spawns;
    }

    public Point getDefaultSpawn() {
        return defaultSpawn;
    }

    public void setDefaultSpawn(Point defaultSpawn) {
        this.defaultSpawn = defaultSpawn;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getWorldType() {
        return worldType;
    }

    public void setWorldType(String worldType) {
        this.worldType = worldType;
    }

    public boolean isAllowAnimals() {
        return allowAnimals;
    }

    public void setAllowAnimals(boolean allowAnimals) {
        this.allowAnimals = allowAnimals;
    }

    public boolean isAllowPvP() {
        return allowPvP;
    }

    public void setAllowPvP(boolean allowPvP) {
        this.allowPvP = allowPvP;
    }

    public boolean isAllowMonsters() {
        return allowMonsters;
    }

    public void setAllowMonsters(boolean allowMonsters) {
        this.allowMonsters = allowMonsters;
    }

    public List<Point2Point> getLaunchPads() {
        return launchPads;
    }

    public void setLaunchPads(List<Point2Point> launchPads) {
        this.launchPads = launchPads;
    }

    public List<Point2Point> getTeleportPads() {
        return teleportPads;
    }

    public void setTeleportPads(List<Point2Point> teleportPads) {
        this.teleportPads = teleportPads;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseMap{");
        sb.append("name='").append(name).append('\'');
        sb.append(", authors=").append(authors);
        sb.append(", spawns=").append(spawns);
        sb.append(", gamemode='").append(gamemode).append('\'');
        sb.append(", defaultSpawn=").append(defaultSpawn);
        sb.append(", environment='").append(environment).append('\'');
        sb.append(", worldType='").append(worldType).append('\'');
        sb.append(", allowAnimals=").append(allowAnimals);
        sb.append(", allowPvP=").append(allowPvP);
        sb.append(", allowMonsters=").append(allowMonsters);
        sb.append('}');
        return sb.toString();
    }
}
