package com.podcrash.api.db.pojos.map;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class IslandsMap extends GameMap {
    private List<Point> greenOres;
    private List<Point> yellowOres;
    private List<Point> blueOres;
    private List<Point> redOres;
    private List<Point> chests;
    private List<BridgePoint> bridgeData;

    private Point middle;

    private String bridgeType;

    @BsonIgnore
    private List<IDPoint2Point> bridges;

    public IslandsMap() {
        super();
        this.greenOres = new ArrayList<>();
        this.yellowOres = new ArrayList<>();
        this.blueOres = new ArrayList<>();
        this.redOres = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.bridges = new ArrayList<>();
    }

    public List<Point> getGreenOres() {
        return greenOres;
    }

    public void setGreenOres(List<Point> greenOres) {
        this.greenOres = greenOres;
    }

    public List<Point> getYellowOres() {
        return yellowOres;
    }

    public void setYellowOres(List<Point> yellowOres) {
        this.yellowOres = yellowOres;
    }

    public List<Point> getBlueOres() {
        return blueOres;
    }

    public void setBlueOres(List<Point> blueOres) {
        this.blueOres = blueOres;
    }

    public List<Point> getRedOres() {
        return redOres;
    }

    public void setRedOres(List<Point> redOres) {
        this.redOres = redOres;
    }

    public List<Point> getChests() {
        return chests;
    }

    public void setChests(List<Point> chests) {
        this.chests = chests;
    }

    public Point getMiddle() {
        return middle;
    }

    public void setMiddle(Point middle) {
        this.middle = middle;
    }

    @BsonIgnore
    public List<IDPoint2Point> getBridges() {
        return bridges;
    }

    @BsonIgnore
    public void setBridges(List<IDPoint2Point> bridges) {
        this.bridges = bridges;
    }

    public List<BridgePoint> getBridgeData() {
        return bridgeData;
    }

    public void setBridgeData(List<BridgePoint> bridgeData) {
        this.bridgeData = bridgeData;
    }

    public String getBridgeType() {
        return bridgeType;
    }

    public void setBridgeType(String bridgeType) {
        this.bridgeType = bridgeType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IslandsMap{");
        sb.append(", greenOres=").append(greenOres.size());
        sb.append(", yellowOres=").append(yellowOres.size());
        sb.append(", blueOres=").append(blueOres.size());
        sb.append(", redOres=").append(redOres.size());
        sb.append(", chests=").append(chests.size());
        sb.append(", spawns=").append(getSpawns());
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", gamemode='").append(getGamemode()).append('\'');
        sb.append(", authors=").append(getAuthors());
        sb.append(", defaultSpawn=").append(getDefaultSpawn());
        sb.append(", environment='").append(getEnvironment()).append('\'');
        sb.append(", worldType='").append(getWorldType()).append('\'');
        sb.append(", allowAnimals=").append(isAllowAnimals());
        sb.append(", allowPvP=").append(isAllowPvP());
        sb.append(", allowMonsters=").append(isAllowMonsters());
        sb.append(", launchPads=").append(getLaunchPads());
        sb.append(", teleportPads=").append(getTeleportPads());
        sb.append(", bridgeData=").append(getBridgeData());
        sb.append('}');
        return sb.toString();
    }
}
