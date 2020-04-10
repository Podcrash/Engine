package com.podcrash.api.db.pojos.map;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class IslandsMap extends GameMap {

    private int waterDmg;
    //All points of sponge with pressure plate on top
    private HashMap<Integer, Location> bridgePoints;
    //Two matching
    private HashMap<Location, Location> bridgeParts;

    //Integer is the # for the piece and LinkedList contains Location, Block
    private HashMap<Integer, LinkedList<Object>> bridgePieces;
    private int bridgeLength;

    public IslandsMap() {
        super();
        this.bridgePoints = new HashMap<Integer, Location>();
        this.bridgeParts = new HashMap<>();
        this.bridgePieces = new HashMap<>();
    }

    public void setBridgeLength(int length) {
        this.bridgeLength = length;
    }

    public int getBridgeLength() {
        return bridgeLength;
    }

    public void addBridgePoint(int bridgeNumber, Location location) {
        this.bridgePoints.put(bridgeNumber, location);
    }

    public HashMap<Integer, Location> getBridgePoints() {
        return bridgePoints;
    }

    public void addBridgePart(Location piece1, Location piece2) {
        this.bridgeParts.put(piece1, piece2);
    }

    public HashMap<Location, Location> getBridgeParts() {
        return bridgeParts;
    }

    public void addBridgePiece(Integer number, LinkedList<Object> locations) {

        bridgePieces.put(number, locations);
    }


    public HashMap<Integer, LinkedList<Object>> getBridgePieces() {
        return bridgePieces;
    }


    public void setWaterDmg(int waterDmg) {
        this.waterDmg = waterDmg;
    }

    public int getWaterDmg() {
        return waterDmg;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("IslandsMap{");
        sb.append("name='").append(getName()).append('\'');
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
