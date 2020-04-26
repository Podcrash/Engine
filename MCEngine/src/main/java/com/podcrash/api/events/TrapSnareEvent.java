package com.podcrash.api.events;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The event is called when a player "picks up" the trap
 */
public class TrapSnareEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Item item;
    private final Player player;
    private boolean cancel = false;

    /**
     * @param item
     * @param player the player who picks it up
     */
    public TrapSnareEvent(Item item, Player player) {
        this.item = item;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     *
     * @param b if false, the item will be cancelled, and all will be fine.
     *          if true, then the trap didn't proc
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
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
