package com.podcrash.api.db.pojos.map;

import java.util.List;
import java.util.StringJoiner;

public class ConquestMap extends BaseMap {
    private List<CapturePointPojo> capturePointPojos;
    private List<Point> emeralds;
    private List<Point> restocks;

    public List<CapturePointPojo> getCapturePointPojos() {
        return capturePointPojos;
    }

    public void setCapturePointPojos(List<CapturePointPojo> capturePointPojos) {
        this.capturePointPojos = capturePointPojos;
    }

    public List<Point> getEmeralds() {
        return emeralds;
    }

    public void setEmeralds(List<Point> emeralds) {
        this.emeralds = emeralds;
    }

    public List<Point> getRestocks() {
        return restocks;
    }

    public void setRestocks(List<Point> restocks) {
        this.restocks = restocks;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConquestMap{");
        sb.append("capturePointPojos=").append(capturePointPojos);
        sb.append(", emeralds=").append(emeralds);
        sb.append(", restocks=").append(restocks);
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
