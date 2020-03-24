package com.podcrash.api.mc.events.econ;

import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PayEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private double moneys;

    public PayEvent(Player player, double moneys) {
        this.player = player;
        this.moneys = moneys;
    }

    /**
     * Note, this returns the balance before the money is given
     * @return
     */
    public double getBalance() {
        return Pluginizer.getSpigotPlugin().getEconomyHandler().getMoney(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
