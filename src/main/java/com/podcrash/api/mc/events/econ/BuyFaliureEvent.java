package com.podcrash.api.mc.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * This happens when a player attempts to buy something but he is too poor :/
 */
public class BuyFaliureEvent extends EconEvent {
    private static final HandlerList handlers = new HandlerList();

    public BuyFaliureEvent(Player buyer, String item, double cost, double currBalance) {
        super(buyer, item, cost, currBalance);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
