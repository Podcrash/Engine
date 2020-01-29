package com.podcrash.api.db.connection;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.podcrash.api.db.TableOrganizer;
import org.bson.Document;

public interface IMongoDoc {
    /**
     * Name of the database
     * TODO: Change based on environment.
     * @return the name
     */
    default String getDatabaseName() {
        return "invicta";
    }

    default MongoClient getClient() {
        return TableOrganizer.getConnection(MongoConnection.class).makeConnection();
    }

    /**
     * Get the database
     * @return the db
     */
    default MongoDatabase getDatabase() {
        return getClient().getDatabase(getDatabaseName());
    }

    default MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }
}
