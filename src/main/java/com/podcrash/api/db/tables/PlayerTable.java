package com.podcrash.api.db.tables;

import com.mongodb.async.SingleResultCallback;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.db.pojos.Currency;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.PojoHelper;
import nu.studer.sample.Tables;
import nu.studer.sample.tables.Players;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerTable extends MongoBaseTable {
    private final Players PLAYERS;
    public PlayerTable(boolean test) {
        super("players");
        this.PLAYERS = Tables.PLAYERS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PLAYERS;
    }

    @Override
    public void createTable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        getCollection().createIndex(Indexes.descending("uuid"), new IndexOptions().unique(true), (res, t) -> {
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
            future.complete(invictaPlayer);
        };
        getCollection(InvictaPlayer.class).find(eq("uuid", uuid)).first(callback);
        return future;
    }
}
