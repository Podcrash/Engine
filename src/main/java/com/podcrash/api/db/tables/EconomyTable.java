package com.podcrash.api.db.tables;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.InsertManyOptions;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.mc.util.ChatUtil;
import org.bson.Document;

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
                    "name", ChatUtil.strip(entry.getKey())).append(
                    "cost", entry.getValue());
            converted.add(doc);
        }
        System.out.println(converted);
        getCollection().insertMany(converted, new InsertManyOptions().ordered(false), (result, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(result);
        });
        return future;
    }

    public CompletableFuture<Double> getCost(String item) {
        item = ChatUtil.strip(item);
        CompletableFuture<Double> costFuture = new CompletableFuture<>();
        getCollection().find(Filters.eq("name", item)).first((res, t) -> {
            DBUtils.handleThrowables(t);
            costFuture.complete((Double) res.get("cost"));
        });

        return costFuture;
    }
    @Override
    public DataTableType getDataTableType() {
        return DataTableType.ECONOMY;
    }
}
