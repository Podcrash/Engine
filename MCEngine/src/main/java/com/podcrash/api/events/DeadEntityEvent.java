package com.podcrash.api.events;

import com.podcrash.api.damage.Cause;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

//o ya reinventing the wheel :D
public class DeadEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;
    private final Cause cause;

    public DeadEntityEvent(Entity entity, Cause cause) {
        this.entity = entity;
        this.cause = cause;
    }

    public Entity getEntity() {
        return entity;
    }

    public Cause getCause() {
        return cause;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
