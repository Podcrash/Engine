package com.podcrash.api.db.connection;

import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

import java.util.Arrays;

public class MongoConnection implements IConnection<MongoClient> {
    private MongoClient client;
    @Override
    public void setUp() {
        String HOST = null, PORT = null, PASSWORD = null;
        System.out.println("[Mongo] Connecting to mongo");
        try {
            HOST = System.getenv("MONGO_HOST");
            PORT = System.getenv("MONGO_PORT");
            PASSWORD = System.getenv("MONGO_PASSWORD");
            System.out.println("[Mongo] Getting Credentials");
        }catch (NullPointerException e) {
            System.out.println("[Mongo] Credentials not found");
            e.printStackTrace();
        }

        final int PORT_INT;

        try {
            if(PORT != null) PORT_INT = Integer.parseInt(PORT);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("[Mongo] PORT is not a number, is: " + PORT);
        }
        ServerAddress address =
                (HOST == null || PORT == null) ?
                new ServerAddress() :
                new ServerAddress(HOST, Integer.parseInt(PORT));

        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(Arrays.asList(address)).build();
        MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();


        System.out.println("[Mongo] Creating connection client");
        this.client = MongoClients.create(settings);
    }

    @Override
    public MongoClient makeConnection() {
        return client;
    }

    @Override
    public void close() {
        client.close();
        client = null;
    }
}
