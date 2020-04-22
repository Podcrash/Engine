package com.podcrash.api.mc.events.econ;

import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PayEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final double moneys;

    private boolean cancel;

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

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
