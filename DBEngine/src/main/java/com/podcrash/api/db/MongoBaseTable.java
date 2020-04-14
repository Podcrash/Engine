package com.podcrash.api.db;

import com.mongodb.async.client.MongoCollection;
import com.podcrash.api.db.connection.IMongoDoc;
import org.bson.Document;

import java.util.concurrent.*;

//TODO:
public abstract class MongoBaseTable implements ITable, IMongoDoc {
    protected final static ExecutorService SERVICE = Executors.newCachedThreadPool();
    private String collectionName;

    public MongoBaseTable(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String getName() {
        return collectionName;
    }

    @Override
    public void createTable(String name) {
        //these do nothing since mongo tables don't need to be created
    }

    @Override
    public void createTable() {
        //these do nothing since mongo tables don't need to be created
    }

    @Override
    public void dropTable(String name) {

    }

    @Override
    public void dropTable() {
        dropTable(getName());
    }

    public MongoCollection<Document> getCollection() {
        return getCollection(getName());
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

    public <T> MongoCollection<T> getCollection(Class<T> tClass) {
        return getDatabase().getCollection(getName(), tClass);
    }

    protected <T> T futureGuaranteeGet(CompletableFuture<T> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        //this should never happen. (Ideally)
        return null;
    }
}
