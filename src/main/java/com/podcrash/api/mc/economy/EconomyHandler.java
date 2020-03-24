package com.podcrash.api.mc.economy;

import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.Currency;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.EconomyTable;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.mc.events.econ.*;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EconomyHandler implements IEconomyHandler {
    private EconomyTable eco;
    private PlayerTable players;

    //this is used to track the orders as it is passing from event to event.
    private Map<String, BuySuccessEvent> currentPlayerOrder;

    /**
     * Set up some of the variables
     */
    public EconomyHandler() {
        eco = TableOrganizer.getTable(DataTableType.ECONOMY);
        players = TableOrganizer.getTable(DataTableType.PLAYERS);

        currentPlayerOrder = new HashMap<>();
    }


    public void pay(Player player, double moneys) {
        Bukkit.getPluginManager().callEvent(new PayEvent(player, moneys));
        players.incrementMoney(player.getUniqueId(), moneys);
    }

    public double getMoney(Player player) {
        try {
            return players.getCurrency(player.getUniqueId()).get(5, TimeUnit.SECONDS).getGold();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return -1;
        //event here
    }

    public boolean containsItem(String item) {
        item = ChatUtil.strip(item);
        try {
            return eco.hasItem(item).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false;
        }
    }
    public CompletableFuture<Boolean> buy(final Player player, String item) {
        item = ChatUtil.strip(item);
        CompletableFuture<Double> costFuture = eco.getCost(item);

        UUID uuid = player.getUniqueId();
        CompletableFuture<Currency> currencyFuture = players.getCurrency(uuid);

        PluginManager pluginManager = Bukkit.getPluginManager();
        String finalItem = item;
        return currencyFuture.thenCombineAsync(costFuture, (currency, oldCost) -> {

            BuyAttemptEvent attempt = new BuyAttemptEvent(player, finalItem, oldCost, currency.getGold());
            pluginManager.callEvent(attempt);
            double cost = attempt.getCost();

            boolean canPay = currency.getGold() > cost;

            if(canPay) {
                BuySuccessEvent success = new BuySuccessEvent(player, finalItem, cost, currency.getGold());
                pluginManager.callEvent(success);
                currentPlayerOrder.put(player.getName(), success);
            } else pluginManager.callEvent(new BuyFaliureEvent(player, finalItem, cost, currency.getGold()));

            return canPay;
        }).exceptionally(t -> {
            DBUtils.handleThrowables(t);
            return false;
        });
    }

    public boolean hasAttempted(Player player, String item) {
        item = ChatUtil.strip(item);
        BuySuccessEvent success = currentPlayerOrder.get(player.getName());
        if(success == null) return false;
        return success.getItem().equalsIgnoreCase(item);
    }
    @Override
    public void confirm(Player player, String item) {
        item = ChatUtil.strip(item);
        BuySuccessEvent success = currentPlayerOrder.get(player.getName());
        if(success == null) return;
        if(!success.getItem().equalsIgnoreCase(item)) return;
        BuyConfirmEvent confirm  = new BuyConfirmEvent(success);
        Bukkit.getPluginManager().callEvent(confirm);
        pay(player, -confirm.getCost());
        currentPlayerOrder.remove(player.getName());
    }
}
