package com.podcrash.api.mc.events.econ;

import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PayEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final double moneys;

    public PayEvent(Player player, double moneys) {
        this.player = player;
        this.moneys = moneys;
    }

    /**
     * Note, this returns the balance before the money is given
     * @return the balance of the player associated with the event
     */
    public double getBalance() {
        return PodcrashSpigot.getInstance().getEconomyHandler().getMoney(player);
    }

    public Player getPlayer() {
        return player;
    }

    public double getMoneys() {
        return moneys;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
