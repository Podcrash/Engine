package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.redis.Communicator;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public interface PodcrashPlugin {
    void redis(RedissonClient client);

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
        Communicator.setup(getExecutorService(), this::redis);
        TableOrganizer.initConnections();
        TableOrganizer.createTables();
    }

    default void closeDBs() {
        Communicator.shutdown();
        TableOrganizer.deleteConnections();
    }

    default CompletableFuture<Void> enableWrap() {
        return CompletableFuture.allOf(
                Communicator.setup(getExecutorService(), this::redis),
            CompletableFuture.runAsync(TableOrganizer::initConnections)
        ).thenRunAsync(TableOrganizer::createTables);
    }

}
