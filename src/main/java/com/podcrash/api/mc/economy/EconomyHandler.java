package com.podcrash.api.mc.economy;

import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.Currency;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.EconomyTable;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class EconomyHandler {
    private EconomyHandler() {

    }

    private static EconomyTable getEcoTable() {
        return TableOrganizer.getTable(DataTableType.ECONOMY);
    }

    private static PlayerTable getPlayerTable() {
        return TableOrganizer.getTable(DataTableType.PLAYERS);
    }

    public static void pay(Player player, double moneys) {
        Pluginizer.getLogger().info("you recieved " + moneys + " gold!");
        getPlayerTable().incrementMoney(player.getUniqueId(), moneys);
        //event here
    }


    public static double getMoney(Player player) {
        try {
            return getPlayerTable().getCurrency(player.getUniqueId()).get(5, TimeUnit.SECONDS).getGold();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return -1;
        //event here
    }

    public static CompletableFuture<Boolean> buy(Player player, String item) {
        CompletableFuture<Double> costFuture = getEcoTable().getCost(item);

        UUID uuid = player.getUniqueId();
        CompletableFuture<Currency> currencyFuture = getPlayerTable().getCurrency(uuid);

        return currencyFuture.thenCombineAsync(costFuture, (currency, cost) -> {
            boolean canPay = currency.getGold() > cost;
            if(canPay) {
                Pluginizer.getLogger().info("you bought " + item);
                getPlayerTable().incrementMoney(uuid, -cost);
            } else {
                Pluginizer.getLogger().info("you did not buy " + item);
            }

            //event that you bought something here
            return canPay;
        }).exceptionally(t -> {
            DBUtils.handleThrowables(t);
            return false;
        });
    }

}
