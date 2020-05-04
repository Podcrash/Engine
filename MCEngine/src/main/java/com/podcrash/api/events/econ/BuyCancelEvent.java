package com.podcrash.api.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BuyCancelEvent extends EconEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel;
    public BuyCancelEvent(Player buyer, String item, double cost, double currBalance) {
        super(buyer, item, cost, currBalance);
    }

    public BuyCancelEvent(BuySuccessEvent success) {
        this(success.getBuyer(), success.getItem(), success.getCost(), success.getCurrentBalance());
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
