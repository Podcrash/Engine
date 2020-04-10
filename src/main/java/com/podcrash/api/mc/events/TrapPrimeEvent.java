package com.podcrash.api.mc.events;

import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the trap is
 * 1) not moving and on the ground
 * 2) has awaited a certain amount of time
 * @see com.podcrash.api.mc.callback.helpers.TrapSetter#spawnTrap(Item item, long time)
 */
public class TrapPrimeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Item item;

    public TrapPrimeEvent(Item item) {
        super(true);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
