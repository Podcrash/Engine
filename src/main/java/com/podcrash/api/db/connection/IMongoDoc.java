package com.podcrash.api.db.connection;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.podcrash.api.db.TableOrganizer;
import org.bson.Document;

public interface IMongoDoc {

    default MongoConnection getMongoConnection() {
        return TableOrganizer.getConnection(MongoConnection.class);
    }

    default MongoClient getClient() {
        return getMongoConnection().getConnection();
    }
    /**
     * Get the database
     * @return the db
     */
    default MongoDatabase getDatabase() {
        return getMongoConnection().getDatabase();
    }

    default MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }
}
