package com.podcrash.api.db.connection;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Arrays;
import java.util.Collections;

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
        CodecRegistry codecRegistry =
                CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                        MongoClientSettings.getDefaultCodecRegistry());

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(address)))
            .codecRegistry(codecRegistry)
            .build();


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
