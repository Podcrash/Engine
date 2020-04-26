package com.podcrash.api.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * This happens after the BuySuccessEvent, where the player just has to
 * confirm his order to buy the item, thereby losing his money
 * at the process
 */
public class BuyConfirmEvent extends EconEvent {
    private static final HandlerList handlers = new HandlerList();

    public BuyConfirmEvent(Player buyer, String item, double cost, double currBalance) {
        super(buyer, item, cost, currBalance);
    }
    public BuyConfirmEvent(BuySuccessEvent success) {
        this(success.getBuyer(), success.getItem(), success.getCost(), success.getCurrentBalance());
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
