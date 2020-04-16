package com.podcrash.api.db.pojos;

import org.bson.types.ObjectId;


public final class Currency {
    private ObjectId objectId;
    private double gold;
    //add other fields here


    public Currency() {

    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Currency{");
        sb.append("objectId=").append(objectId);
        sb.append(", gold=").append(gold);
        sb.append('}');
        return sb.toString();
    }
}
