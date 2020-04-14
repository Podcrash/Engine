package com.podcrash.api.db.pojos.map;

import java.util.*;

public class ConquestMap extends GameMap {
    private List<CapturePointPojo> capturePointPojos;
    private List<Point> stars;
    private List<Point> diamonds;
    private List<Point> mines;
    private List<Point> restocks;

    public ConquestMap() {
        super();
        this.capturePointPojos = new ArrayList<>();
        this.stars = new ArrayList<>();
        this.diamonds = new ArrayList<>();
        this.mines = new ArrayList<>();
        this.restocks = new ArrayList<>();
    }

    public List<CapturePointPojo> getCapturePointPojos() {
        return capturePointPojos;
    }

    public void setCapturePointPojos(List<CapturePointPojo> capturePointPojos) {
        this.capturePointPojos = capturePointPojos;
    }

    public List<Point> getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(List<Point> diamonds) {
        this.diamonds = diamonds;
    }

    public List<Point> getMines() {
        return mines;
    }

    public void setMines(List<Point> mines) {
        this.mines = mines;
    }

    public List<Point> getStars() {
        return stars;
    }

    public void setStars(List<Point> stars) {
        this.stars = stars;
    }

    public List<Point> getRestocks() {
        return restocks;
    }

    public void setRestocks(List<Point> restocks) {
        this.restocks = restocks;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("ConquestMap{");
        sb.append("capturePointPojos=").append(capturePointPojos);
        sb.append(", stars=").append(stars);
        sb.append(", diamonds=").append(diamonds);
        sb.append(", mines=").append(mines);
        sb.append(", restocks=").append(restocks);
        sb.append(", launchPads=").append(getLaunchPads());
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
