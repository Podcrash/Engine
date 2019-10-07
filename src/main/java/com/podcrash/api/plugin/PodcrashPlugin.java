package com.podcrash.api.plugin;

import com.podcrash.api.db.DatabaseConnection;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.redis.Communicator;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutorService;

public interface PodcrashPlugin {
    void redis(RedissonClient client);

    ExecutorService getExecutorService();

    default void enable() {
        connectDbs();
    }

    default void disable() {
        closeDBs();
        Pluginizer.destroy();
    }
    default void connectDbs() {
        Communicator.setup(getExecutorService(), this::redis);
        TableOrganizer.createTables(false);
    }

    default void closeDBs() {
        Communicator.shutdown();
        DatabaseConnection.close();
    }

}
