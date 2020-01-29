package com.podcrash.api.db.pojos;

import org.bson.types.ObjectId;

public interface GameData {
    ObjectId getObjectId();
    String getName();

    void setObjectId(ObjectId objectId);

}
