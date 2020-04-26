package com.podcrash.api.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * This happens when a player attempts to buy something but he is too poor :/
 */
public class BuyFailureEvent extends EconEvent {
    private static final HandlerList handlers = new HandlerList();

    public BuyFailureEvent(Player buyer, String item, double cost, double currBalance) {
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
