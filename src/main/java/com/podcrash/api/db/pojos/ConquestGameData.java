package com.podcrash.api.db.pojos;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class ConquestGameData implements GameData {
    private ObjectId objectId;

    private List<String> allowedSkills;
    private Map<String, String> builds;

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

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

    public Map<String, String> getBuilds() {
        return builds;
    }

    public void setBuilds(Map<String, String> builds) {
        this.builds = builds;
    }
}
