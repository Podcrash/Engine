package com.podcrash.api.mc.events;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The event is called when a player "picks up" the trap
 */
public class TrapSnareEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Item item;
    private final Player player;

    /**
     * @param item
     * @param player the player who picks it up
     */
    public TrapSnareEvent(Item item, Player player) {
        this.item = item;
        this.player = player;
    }

    public Item getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
