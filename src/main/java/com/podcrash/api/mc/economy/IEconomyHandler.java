package com.podcrash.api.mc.economy;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface IEconomyHandler {
    /**
     * Give the player a select amount of money (increment)
     * @param player
     * @param moneys
     */
    void pay(Player player, double moneys);

    /**
     * Get the amount of money a player has
     * @param player
     * @return
     */
    double getMoney(Player player);

    /**
     * Get the item, and sell the amount of money the player has.
     * Will call a bunch of events
     * @param player
     * @param item
     * @return
     */
    CompletableFuture<Boolean> buy(Player player, String item);

    /**
     * Confirm an order
     * @param player
     */
    void confirm(Player player, String item);
}
