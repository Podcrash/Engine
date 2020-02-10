package com.podcrash.api.db.pojos;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.List;

public class ConquestGameData implements GameData {
    private ObjectId objectId;

    private List<String> allowedSkills;
    private Document builds;

    public ConquestGameData() {
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    @BsonIgnore
    @Override
    public String getName() {
        return "conquest";
    }

    public List<String> getAllowedSkills() {
        return allowedSkills;
    }

    public void setAllowedSkills(List<String> allowedSkills) {
        this.allowedSkills = allowedSkills;
    }

    public Document getBuilds() {
        return builds;
    }

    public void setBuilds(Document builds) {
        this.builds = builds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConquestGameData{");
        sb.append("objectId=").append(objectId);
        sb.append(", allowedSkills=").append(allowedSkills);
        sb.append(", builds=").append(builds);
        sb.append('}');
        return sb.toString();
    }
}
