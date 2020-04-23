package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.redis.Communicator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class PodcrashPlugin extends JavaPlugin {

    public static final boolean DEBUG = true;

    public abstract ExecutorService getExecutorService();

    public void enable() {
        connectDbs();
    }

    public void disable() {
        closeDBs();
    }
    public void connectDbs() {
        Communicator.setup(getExecutorService());
        TableOrganizer.initConnections();
        TableOrganizer.createTables();
    }

    public void closeDBs() {
        Communicator.shutdown();
        TableOrganizer.deleteConnections();
    }

    public CompletableFuture<Void> enableWrap() {
        CompletableFuture<Void> setUpRedis = Communicator.setup(getExecutorService());
        CompletableFuture<Void> setUpMongo = CompletableFuture.runAsync(TableOrganizer::initConnections).thenRunAsync(TableOrganizer::createTables);

        return CompletableFuture.allOf(setUpMongo, setUpRedis);
    }

}
