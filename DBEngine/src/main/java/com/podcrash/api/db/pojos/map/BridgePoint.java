package com.podcrash.api.db.pojos.map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BridgePoint {
    private int bridgeID;
    private List<BridgeSection> sections;

    public BridgePoint() {
    }

    public int getBridgeID() {
        return bridgeID;
    }

    public void setBridgeID(int bridgeID) {
        this.bridgeID = bridgeID;
    }

    public List<BridgeSection> getSections() {
        return sections;
    }

    public void setSections(List<BridgeSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "BridgePoint{" + "bridgeID=" + bridgeID +
                ", sections=" + sections +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BridgePoint)) return false;

        BridgePoint that = (BridgePoint) o;

        if (bridgeID != that.bridgeID) return false;
        return Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        int result = bridgeID;
        result = 31 * result + (sections != null ? sections.hashCode() : 0);
        return result;
    }
}
