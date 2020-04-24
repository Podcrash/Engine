package com.podcrash.api.mc.events;

import com.podcrash.api.mc.damage.Cause;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The distinction between this one and DamageApplyEvent, is that the event is not applied by other sources.
 */
public class DamageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity victim;
    private final double damage;
    private final Cause cause;

    private boolean cancel;

    public DamageEvent(LivingEntity victim, double damage, Cause cause) {
        this.victim = victim;
        this.damage = damage;
        this.cause = cause;
        this.cancel = false;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
