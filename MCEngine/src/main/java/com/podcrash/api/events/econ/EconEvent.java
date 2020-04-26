package com.podcrash.api.events.econ;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class EconEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player buyer;
    private final String item;
    private double cost;
    private final double currBalance;

    private List<String> description;

    public EconEvent(Player buyer, String item, double cost, double currBalance) {
        super();
        this.buyer = buyer;
        this.item = item;
        this.cost = cost;
        this.currBalance = currBalance;
        this.description = new ArrayList<>();
    }

    public Player getBuyer() {
        return buyer;
    }

    public String getItem() {
        return item;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCurrentBalance() {
        return currBalance;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
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
        if (this == o)
            return true;
        if (!(o instanceof EconEvent))
            return false;

        EconEvent econEvent = (EconEvent) o;

        if (Double.compare(econEvent.cost, cost) != 0)
            return false;
        if (Double.compare(econEvent.currBalance, currBalance) != 0)
            return false;
        if (!buyer.equals(econEvent.buyer))
            return false;
        if (!item.equals(econEvent.item))
            return false;
        return description.equals(econEvent.description);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = buyer.hashCode();
        result = 31 * result + item.hashCode();
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(currBalance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + description.hashCode();
        return result;
    }
}
