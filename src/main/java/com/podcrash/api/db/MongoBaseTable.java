package com.podcrash.api.db;

import com.mongodb.client.MongoCollection;
import com.podcrash.api.db.connection.IMongoDoc;
import org.bson.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO:
public abstract class MongoBaseTable implements ITable, IMongoDoc {
    protected final static ExecutorService SERVICE = Executors.newFixedThreadPool(4);
    private String collectionName;
    private boolean test;

    public MongoBaseTable(String collectionName, boolean test) {
        this.test = test;
        this.collectionName = (test) ? collectionName + "test" : collectionName;
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
        return getDatabase().getCollection(getName());
    }
}
