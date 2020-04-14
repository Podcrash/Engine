package com.podcrash.api.mc.events;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemCollideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private LivingEntity collisionVictim;
    private Item item;
    private boolean cancel;

    public ItemCollideEvent(LivingEntity collisionVictim, Item item) {
        this.collisionVictim = collisionVictim;
        this.item = item;
        this.cancel = false;
    }

    public LivingEntity getCollisionVictim() {
        return collisionVictim;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
