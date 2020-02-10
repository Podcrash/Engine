package com.podcrash.api.db.connection;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.podcrash.api.db.pojos.*;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Arrays;
import java.util.Collections;

public class MongoConnection implements IConnection<MongoClient> {
    private MongoClient client;

    private MongoCredential getCredential() {
        String user = System.getenv("MONGO_USER");
        String password = System.getenv("MONGO_PASSWORD");
        String databaseName = System.getenv("INVICTA_DATABASE_NAME");

        return MongoCredential.createCredential(user, databaseName, password.toCharArray());
    }
    private PojoCodecProvider getPojoCodec() {
        ClassModel<GameData> gameDataModel = ClassModel.builder(GameData.class).enableDiscriminator(true).build();
        ClassModel<ConquestGameData> conquestDataModel = ClassModel.builder(ConquestGameData.class).enableDiscriminator(true).build();

        ClassModel<InvictaPlayer> playerModel = ClassModel.builder(InvictaPlayer.class).enableDiscriminator(true).build();
        ClassModel<Rank> rankModel = ClassModel.builder(Rank.class).enableDiscriminator(false).build();
        ClassModel<Currency> currencyModel = ClassModel.builder(Currency.class).enableDiscriminator(false).build();

        ClassModel<?>[] models = new ClassModel[] {
            playerModel, rankModel, currencyModel,
            gameDataModel, conquestDataModel,
        };
        return PojoCodecProvider.builder()
            .register(models)
            .automatic(true)
            .build();
    }
    @Override
    public void setUp() {
        String HOST = null, PORT = null;
        System.out.println("[Mongo] Connecting to mongo");
        try {
            HOST = System.getenv("MONGO_HOST");
            PORT = System.getenv("MONGO_PORT");
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
        PojoCodecProvider pojoProvider = getPojoCodec();
        CodecRegistry codecRegistry =
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
            CodecRegistries.fromProviders(pojoProvider));

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(address)))
            .codecRegistry(codecRegistry)
            .build();


        System.out.println("[Mongo] Creating connection client");
        this.client = MongoClients.create(settings);

        client.getDatabase("invicta");
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
