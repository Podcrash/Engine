package com.podcrash.api.db;


import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.podcrash.api.plugin.Pluginizer;
import nu.studer.sample.Tables;
import nu.studer.sample.tables.Players;
import org.bson.Document;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerTable extends MongoBaseTable {
    private final Players PLAYERS;
    public PlayerTable(boolean test) {
        super("players", test);
        this.PLAYERS = Tables.PLAYERS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PLAYERS;
    }

    @Override
    public void createTable() {
        getCollection().createIndex(Indexes.descending("uuid"),
                new IndexOptions().unique(true), ((result, t) -> {
            DBUtils.handleThrowables(t);
            Pluginizer.getLogger().info(result);
        }));
    }

    public void insert(UUID uuid) {
        Document insert = new Document("uuid", uuid);
        CompletableFuture<Void> future = new CompletableFuture<>();
        getCollection().insertOne(insert, (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Document getPlayerDocumentSync(UUID uuid) {
        CompletableFuture<Document> future = getPlayerDocumentAsync(uuid);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Pluginizer.getLogger().info("PlayerTable: getPlayerDocumentSync returned null?");
        return null;
    }

    public CompletableFuture<Document> getPlayerDocumentAsync(UUID uuid) {
        CompletableFuture<Document> future = new CompletableFuture<>();
        getCollection().find(Filters.eq("uuid", uuid)).first((res, t) -> {
            DBUtils.handleThrowables(t);
            if (res == null) throw new IllegalStateException("the uuid had to be inserted when the player joins");
            future.complete(res);
        });

        return future;
    }
}
