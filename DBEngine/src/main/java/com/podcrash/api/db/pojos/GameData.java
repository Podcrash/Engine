package com.podcrash.api.db.pojos;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

public interface GameData {
    ObjectId getObjectId();

    @BsonIgnore
    String getName();

    void setObjectId(ObjectId objectId);

}
