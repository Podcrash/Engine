package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.redis.Communicator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public interface PodcrashPlugin {

    ExecutorService getExecutorService();
    Logger getLogger();

    default void enable() {
        connectDbs();
    }

    default void disable() {
        closeDBs();
        Pluginizer.destroy();
    }
    default void connectDbs() {
        Communicator.setup(getExecutorService());
        TableOrganizer.initConnections();
        TableOrganizer.createTables();
    }

    default void closeDBs() {
        Communicator.shutdown();
        TableOrganizer.deleteConnections();
    }

    default CompletableFuture<Void> enableWrap() {
        CompletableFuture<Void> setUpRedis = Communicator.setup(getExecutorService());
        CompletableFuture<Void> setUpMongo = CompletableFuture.runAsync(TableOrganizer::initConnections).thenRunAsync(TableOrganizer::createTables);

        return CompletableFuture.allOf(setUpMongo, setUpRedis);
    }

}
