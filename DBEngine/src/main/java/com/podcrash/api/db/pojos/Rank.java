package com.podcrash.api.db.pojos;

import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Rank {
    private ObjectId objectId;
    private String name;
    private String color;
    private int position;

    private Set<String> permissions;

    public Rank() {
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color + ChatColor.BOLD.toString();
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashSet<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rank{");
        sb.append("objectId=").append(objectId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", position=").append(position);
        sb.append(", permissions=").append(permissions);
        sb.append('}');
        return sb.toString();
    }
}
