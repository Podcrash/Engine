package com.podcrash.api.db.tables;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;

import static com.mongodb.client.model.Filters.*;

import com.podcrash.api.db.pojos.Currency;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.plugin.Pluginizer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerTable extends MongoBaseTable {
    public PlayerTable() {
        super("players");
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PLAYERS;
    }

    @Override
    public void createTable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        getCollection().createIndex(Indexes.compoundIndex(Indexes.descending("uuid"), Indexes.ascending("currency.gold")), new IndexOptions().unique(true), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public CompletableFuture<Void> insert(UUID uuid) {
        InvictaPlayer player = PojoHelper.createInvictaPlayer(uuid);

        CompletableFuture<Void> future = new CompletableFuture<>();
        getCollection(InvictaPlayer.class).insertOne(player, (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });

        return future;
    }

    public InvictaPlayer getPlayerDocumentSync(UUID uuid) {
        CompletableFuture<InvictaPlayer> future = getPlayerDocumentAsync(uuid);
        return futureGuaranteeGet(future);
    }

    public CompletableFuture<InvictaPlayer> getPlayerDocumentAsync(UUID uuid) {
        CompletableFuture<InvictaPlayer> future = new CompletableFuture<>();
        SingleResultCallback<InvictaPlayer> callback = (invictaPlayer, t) -> {
            DBUtils.handleThrowables(t);
            Pluginizer.getLogger().info("returned invicta player: " + invictaPlayer);
            future.complete(invictaPlayer);
        };
        getCollection(InvictaPlayer.class).find(eq("uuid", uuid)).first(callback);
        return future;
    }

    public CompletableFuture<UpdateResult> incrementMoney(UUID uuid, double value){
        CompletableFuture<UpdateResult> res = new CompletableFuture<>();
        getCollection().updateOne(eq("uuid", uuid), Updates.inc("currency.gold", value), ((result, t) -> {
            DBUtils.handleThrowables(t);
            res.complete(result);
        }));

        return res;
    }

    public CompletableFuture<Currency> getCurrency(UUID uuid) {
        CompletableFuture<Currency> currencyCompletableFuture = new CompletableFuture<>();
        getCollection(InvictaPlayer.class).find(eq("uuid", uuid)).projection(Projections.include("currency")).first((result, t) -> {
            DBUtils.handleThrowables(t);
            currencyCompletableFuture.complete(result.getCurrency());
        });

        return currencyCompletableFuture;
    }
}
