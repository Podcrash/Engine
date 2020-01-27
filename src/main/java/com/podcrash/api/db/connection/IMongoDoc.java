package com.podcrash.api.db.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.podcrash.api.db.TableOrganizer;
import org.bson.Document;

public interface IMongoDoc {
    /**
     * Name of the database
     * @return the name
     */
    String getName();

    default MongoClient getClient() {
        return TableOrganizer.getConnection(MongoConnection.class).makeConnection();
    }

    /**
     * Get the database
     * @return the db
     */
    default MongoDatabase getDatabase() {
        return getClient().getDatabase(getName());
    }

    default MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }


}
