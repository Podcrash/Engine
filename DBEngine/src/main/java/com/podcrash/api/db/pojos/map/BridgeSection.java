package com.podcrash.api.db.pojos.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BridgeSection {
    private double x, z;

    private Map<String, Object> blockMap;

    public BridgeSection() {
        this.blockMap = new HashMap<>();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Map<String, Object> getBlockMap() {
        return blockMap;
    }

    public void setBlockMap(Map<String, Object> blockMap) {
        this.blockMap = blockMap;
    }

    @Override
    public String toString() {
        String sb = "BridgeSection{" + "x=" + x +
                ", z=" + z +
                ", blockMap=" + blockMap +
                '}';
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BridgeSection)) return false;

        BridgeSection section = (BridgeSection) o;

        if (Double.compare(section.x, x) != 0) return false;
        if (Double.compare(section.z, z) != 0) return false;
        return Objects.equals(blockMap, section.blockMap);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (blockMap != null ? blockMap.hashCode() : 0);
        return result;
    }
}
