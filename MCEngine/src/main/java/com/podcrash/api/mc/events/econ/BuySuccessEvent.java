package com.podcrash.api.mc.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * BuyEvents only pass if the player can pay for it.
 */
public class BuySuccessEvent extends EconEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel;
    public BuySuccessEvent(Player buyer, String item, double cost, double currBalance) {
        super(buyer, item, cost, currBalance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuySuccessEvent)) return false;
        if (!super.equals(o)) return false;

        BuySuccessEvent event = (BuySuccessEvent) o;

        return cancel == event.cancel;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (cancel ? 1 : 0);
        return result;
    }
}
