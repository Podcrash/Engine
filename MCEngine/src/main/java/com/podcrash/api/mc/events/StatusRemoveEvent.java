package com.podcrash.api.mc.events;

import com.podcrash.api.mc.effect.status.Status;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatusRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private LivingEntity livingEntity;
    private Status status;

    public StatusRemoveEvent(LivingEntity entity, Status status) {
        this.livingEntity = entity;
        this.status = status;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public Status getStatus() {
        return status;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
