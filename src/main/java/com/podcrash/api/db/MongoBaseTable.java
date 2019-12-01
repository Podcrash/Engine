package com.podcrash.api.db;

import com.podcrash.api.db.connection.IMongoDoc;

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
        getClient().getDatabase(name).drop((res, throwable) -> {
            if(throwable != null) throwable.printStackTrace();
        });
    }

    @Override
    public void dropTable() {
        dropTable(getName());
    }
}
