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
        getCollection().insertOne(entry, ((result, t) -> {
            if(t == null) Pluginizer.getSpigotPlugin().getLogger().info(t.getLocalizedMessage());
            future.complete(null);
        }));

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private int get(String sequenceName) {
        CompletableFuture future = new CompletableFuture();
        getCollection().find(Filters.eq("name", sequenceName)).first((res, t) -> {
            Integer value;
            if(t != null) {
                Pluginizer.getSpigotPlugin().getLogger().info(t.getLocalizedMessage());
                value = -1;
            }else value = (Integer) res.get("value");
            future.complete(value);
        });
        try {
            return (int) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("sequence getting failed");
    }

    private void increment(String sequenceName) {
        CompletableFuture future = new CompletableFuture();
        getCollection().find(Filters.eq("name", sequenceName)).first((res, t) -> {
            if(t != null) {
                Pluginizer.getSpigotPlugin().getLogger().info(t.getLocalizedMessage());
                future.complete(new IllegalStateException("sequence incrementing failed"));
            }

            int value = (int) res.get("value");
            res.put("value", value + 1);
            future.complete(value);
        });
        Object get = null;
        try {
            get = future.get();
            if(get instanceof Throwable) {
                Pluginizer.getSpigotPlugin().getLogger().info(((Throwable) get).getLocalizedMessage());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.SEQUENCES;
    }
}
