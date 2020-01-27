package com.podcrash.api.db;

import com.mongodb.client.model.Filters;
import com.podcrash.api.db.connection.IMongoDoc;
import com.podcrash.api.plugin.Pluginizer;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * All of this is synchronous as to not mess up any data integrity
 */
class SequenceTable extends MongoBaseTable implements IMongoDoc {

    public SequenceTable(boolean test) {
        super("sequences", test);
    }

    private void insert(String sequenceName) {
        CompletableFuture future = new CompletableFuture();
        Document entry = new Document()
                .append("name", sequenceName)
                .append("value", 0);
        getCollection().insertOne(entry);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private int get(String sequenceName) {
        Document res = getCollection().find(Filters.eq("name", sequenceName)).first();
        return (int) res.get("value");
    }

    private void increment(String sequenceName) {
        Document incrementValue = new Document("value", 1);
        getCollection().updateOne(Filters.eq("name", sequenceName), new Document("$inc", incrementValue));
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.SEQUENCES;
    }
}
