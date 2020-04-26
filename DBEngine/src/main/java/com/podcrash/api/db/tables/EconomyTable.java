package com.podcrash.api.db.tables;

import com.mongodb.client.model.*;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EconomyTable extends MongoBaseTable {
    public EconomyTable() {
        super("economy");
    }

    @Override
    public void createTable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        getCollection().createIndex(Indexes.descending("name"), new IndexOptions().unique(true), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public CompletableFuture<Void> putItem(Map<String, Double> itemCosts) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        List<Document> converted = new ArrayList<>();
        for(Map.Entry<String, Double> entry : itemCosts.entrySet()) {
            Document doc = new Document(
                    "name", strip(entry.getKey())).append(
                    "cost", entry.getValue());
            converted.add(doc);
        }
        System.out.println(converted);
        getCollection().insertMany(converted, new InsertManyOptions().ordered(false), (result, t) -> {
            DBUtils.handleDuplicateKeyException(t);
            future.complete(result);
        });
        return future;
    }

    public CompletableFuture<Boolean> hasItem(String item) {
        item = strip(item);
        CompletableFuture<Boolean> bool = new CompletableFuture<>();
        getCollection().find(Filters.eq("name", item))
            .projection(Projections.fields(Projections.include("name"), Projections.excludeId()))
            .first(((result, t) -> {
            DBUtils.handleThrowables(t);
            bool.complete(result != null);
        }));

        return bool;
    }
    public CompletableFuture<Double> getCost(String item) {
        item = strip(item);
        CompletableFuture<Double> costFuture = new CompletableFuture<>();
        getCollection()
            .find(Filters.eq("name", item))
            .projection(Projections.fields(Projections.include("cost"), Projections.excludeId()))
            .first((res, t) -> {
            DBUtils.handleThrowables(t);
            if (res == null) costFuture.complete(0D);
            else costFuture.complete((Double) res.get("cost"));
        });

        return costFuture;
    }

    private String purge(String s) {
        char[] charAR = s.toCharArray();
        int size = charAR.length;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if (charAR[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            builder.append(charAR[i]);
        }
        return builder.toString();
    }

    //modularize even and make a common util
    /**
     * Strip the parametric string into only its alphabetical characters
     * and lowercased.
     */
    private String strip(String str) {
        str = purge(str);
        return str.replaceAll("/([^A-z]*)/g", str).replaceAll(" ", "").toLowerCase();
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.ECONOMY;
    }
}
