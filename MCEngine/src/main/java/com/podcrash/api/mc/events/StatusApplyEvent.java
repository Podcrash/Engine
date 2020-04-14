package com.podcrash.api.mc.events;

import com.podcrash.api.mc.effect.status.Status;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatusApplyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private boolean modified;

    private LivingEntity livingEntity;
    private Status status;
    private float duration;
    private int potency;

    public StatusApplyEvent(LivingEntity player, Status status, float duration, int potency) {
        this.livingEntity = player;
        this.status = status;
        this.duration = duration;
        this.potency = potency;
        this.modified = false;
    }

    public LivingEntity getEntity() {
        return livingEntity;
    }

    public Status getStatus() {
        return status;
    }

    public float getDuration() {
        return duration;
    }

    public int getPotency() {
        return potency;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setPotency(int potency) {
        this.potency = potency;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public boolean isModified(){
        return modified;
    }
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
