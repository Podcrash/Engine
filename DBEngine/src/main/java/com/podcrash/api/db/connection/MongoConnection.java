package com.podcrash.api.db.connection;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.podcrash.api.db.pojos.*;
import com.podcrash.api.db.pojos.map.*;
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
    private String databaseName;

    private MongoCredential getCredential() {
        String user = System.getenv("MONGO_USER");
        String password = System.getenv("MONGO_PASSWORD");
        databaseName = System.getenv("INVICTA_DATABASE_NAME");

        return MongoCredential.createCredential(user, databaseName, password.toCharArray());
    }

    /**
     * TODO: annotations with reflection pls
     * @return
     */
    private PojoCodecProvider getPojoCodec() {
        ClassModel<GameData> gameDataModel = getClassModel(GameData.class, true);
        ClassModel<ConquestGameData> conquestDataModel = getClassModel(ConquestGameData.class, true);

        ClassModel<InvictaPlayer> playerModel = getClassModel(InvictaPlayer.class, true);
        ClassModel<Rank> rankModel = getClassModel(Rank.class, false);
        ClassModel<Currency> currencyModel = getClassModel(Currency.class, false);

        ClassModel<BaseMap> baseMapModel = getClassModel(BaseMap.class, true);
        ClassModel<GameMap> gameMapModel = getClassModel(GameMap.class, true);
        ClassModel<Point> pointModel = getClassModel(Point.class, true);
        ClassModel<Point2Point> point2Model = getClassModel(Point2Point.class, true);
        ClassModel<CapturePointPojo> capturePointModel = getClassModel(CapturePointPojo.class, true);

        ClassModel<ConquestMap> conquestMapModel = getClassModel(ConquestMap.class, true);
        ClassModel<IslandsMap> islandsMapModel1 = getClassModel(IslandsMap.class, true);

        ClassModel<?>[] models = new ClassModel[] {
            playerModel, rankModel, currencyModel,
            gameDataModel, conquestDataModel,
            baseMapModel, pointModel, point2Model, capturePointModel,
            gameMapModel, conquestMapModel, islandsMapModel1
        };
        return PojoCodecProvider.builder()
            .register(models)
            .automatic(true)
            .build();
    }

    /**
     * simple helper method to generate a classmodel
     * @param clazz
     * @param discriminator
     * @param <T>
     * @return
     */
    private <T> ClassModel<T> getClassModel(Class<T> clazz, boolean discriminator) {
        return ClassModel.builder(clazz).enableDiscriminator(discriminator).build();
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
            if (PORT != null) PORT_INT = Integer.parseInt(PORT);
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
            .credential(getCredential())
            .build();


        System.out.println("[Mongo] Creating connection client");
        this.client = MongoClients.create(settings);
    }


    @Override
    public MongoClient getConnection() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return client.getDatabase(databaseName);
    }

    @Override
    public void close() {
        client.close();
        client = null;
    }
}
