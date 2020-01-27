package com.podcrash.api.db;


import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.podcrash.api.plugin.Pluginizer;
import nu.studer.sample.Tables;
import nu.studer.sample.tables.Players;
import org.bson.Document;
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
                new IndexOptions().unique(true));
    }

    public void insert(UUID uuid) {
        Document insert = new Document("uuid", uuid);
        try {
            getCollection().insertOne(insert);
        }catch (MongoWriteException e) { //e probably because of duplicate uuid insert
            //do nothing
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
        future.complete(getCollection().find(Filters.eq("uuid", uuid)).first());
        return future;
    }
}
