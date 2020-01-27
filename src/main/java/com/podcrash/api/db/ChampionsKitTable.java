package com.podcrash.api.db;

//static imports are recommended to make the code look cleaner

import com.mongodb.client.model.Filters;
import com.podcrash.api.plugin.Pluginizer;
import nu.studer.sample.Tables;
import nu.studer.sample.tables.Kits;
import org.bson.Document;
import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * clasz, build_id, jsondata --> clasz + build_id, jsondata
 */
public class ChampionsKitTable extends MongoBaseTable implements IPlayerDB {
    private final Kits KITS;
    public ChampionsKitTable(boolean test) {
        super("championskits", test);
        this.KITS = Tables.KITS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.KITS;
    }

    @Override
    public void createTable() {

    }


    /**
     * Adds the championskits column to the players table if they don't already have it
     * @param uuid
     */
    private void evaluate(UUID uuid) {
        Document playerDoc = getPlayerDocumentSync(uuid);
        Logger log = Pluginizer.getLogger();
        log.info(playerDoc.toString());
        if(playerDoc.containsKey(getName())) return;
        Document kitDocument = new Document();
        Document addColumn = new Document(getName(), kitDocument);

        log.info("updating?");
        log.info(addColumn.toString());
        CompletableFuture<Document> future = new CompletableFuture<>();
        getPlayerTable().getCollection().findOneAndUpdate(playerDoc, new Document("$set", addColumn), ((result, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(result);
        }));
        try {
            Document futureDoc = future.get();
            if(futureDoc != null) log.info(future.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    private CompletableFuture<Document> getKitDocumentAsync(UUID uuid) {
        evaluate(uuid);
        return getPlayerDocumentAsync(uuid).thenApplyAsync(doc -> (Document) doc.get(getName()));
    }
    private Document getKitDocumentSync(UUID uuid) {
        try {
            return getKitDocumentAsync(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("getKitDocumentSync must not be null");
    }

    public CompletableFuture<String> getJSONDataAsync(UUID uuid, String clasz, int build_id) {
        //TODO: Find out if this works
        CompletableFuture<Document> kitDocument = getKitDocumentAsync(uuid);
        return kitDocument.thenApplyAsync((kits -> (String) kits.get(clasz + build_id)), SERVICE);
    }
    public String getJSONData(UUID uuid, String clasz, int build_id) {
        CompletableFuture<String> data = getJSONDataAsync(uuid, clasz, build_id);
        try {
            return data.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("getJSONData sync failed");
    }

    private void updateSync(Document playerDocument, Document updated) {
        CompletableFuture<Document> future = new CompletableFuture<>();
        getCollection().findOneAndUpdate(playerDocument, updated, ((result, t) -> {
            DBUtils.handleThrowables(t);
            Pluginizer.getLogger().info(playerDocument.toString());
            Pluginizer.getLogger().info(updated.toString());
            future.complete(result);
        }));

        try {
            Document futureDoc = future.get();
            Pluginizer.getLogger().info("update sync!");
            if(futureDoc != null) Pluginizer.getLogger().info(futureDoc.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void set(UUID uuid, String clasz, int build_id, String data) {
        Document kitDocument = getKitDocumentSync(uuid);
        Document set = new Document(kitDocument)
                .append(clasz + build_id, data);

        Document playerDocument = getPlayerDocumentSync(uuid);
        Document updated = new Document(getName(), set);

        Logger log = Pluginizer.getLogger();
        log.info("Setting: \n" + playerDocument.toString() + '\n' + updated.toString());
        updateSync(playerDocument, new Document("$set", updated));
    }

    public void alter(UUID uuid, String clasz, int build_id, String data) {
        set(uuid, clasz, build_id, data);
    }

    public void delete(UUID uuid, String clasz, int build_id) {
        Document kitDocument = getKitDocumentSync(uuid);
        Document set = new Document(kitDocument);
        set.remove(clasz + build_id);

        Document playerDocument = getPlayerDocumentSync(uuid);
        Document updated = new Document(getName(), set);

        updateSync(playerDocument, new Document("$set", updated));
    }

}
