package com.podcrash.api.mc.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class Disguise {
    private final Entity entity;
    private final EntityType entityType;
    private final Entity seen;

    public Disguise(Entity entity, EntityType entityType, Entity seen) {
        this.entity = entity;
        this.entityType = entityType;
        this.seen = seen;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Entity getSeen() {
        return seen;
    }

    @Override
    public int hashCode() {
        return entity.getEntityId();
    }
}
