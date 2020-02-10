package com.podcrash.api.db.redis;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * WARNING:
 * To avoid confusion, all keys are lower-cased.
 * DO NOT USE A SINGLE CAPITAL LETTER (for keys).
 */
public class Communicator {
    private static final String CACHE_CHANNEL = "PC-CACHE";
    private static RedissonClient client;
    private static String code;
    private static RTopic controllerMessages;

    public static CompletableFuture<Void> setup(Executor executor) {
        return setup(executor, (client) -> {});
    }
    public static CompletableFuture<Void> setup(Executor executor, Consumer<RedissonClient> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[Redis] Starting!");
            Config config = new Config();
            String[] creds = getCredentials();
            System.out.println("[Redis] Credentials: " + creds[0] + " " + creds[1].replaceAll(".", "*"));
            config.useSingleServer()
                    .setAddress(creds[0])
                    .setPassword(creds[1])
                    .setConnectionMinimumIdleSize(1)
                    .setConnectionPoolSize(2);

            client = Redisson.create(config);

            controllerMessages = client.getTopic("controller-messages");
            System.out.println("[Redis] Setting Controller Messages!");
            listeners();
            System.out.println("[Redis] Ready!");
            return client;
        }, executor).thenAcceptAsync(consumer, executor);
    }

    public static boolean isReady() {
        return client != null;
    }
    public static boolean isGameLobby(){
        return code != null;
    }
    //USE THESE METHODS BELOW FOR CACHING
    public static void cache(String key, String value) {
        cache(CACHE_CHANNEL, key, value);
    }
    public static String getCacheValue(String key) {
        return getCacheValue(CACHE_CHANNEL, key);
    }
    public static boolean containsKey(String key) {
        return containsKey(CACHE_CHANNEL, key);
    }
    public static void removeCache(String key) {
        removeCache(CACHE_CHANNEL, key);
    }
    public static List<String> getCacheKeys() {
        return getCacheKeys(CACHE_CHANNEL);
    }
    public static List<String> getCacheValues() {
        return getCacheValues(CACHE_CHANNEL);
    }
    public static Map<String, String> getCacheMap() {
        return getCacheMap(CACHE_CHANNEL);
    }
    public static void cacheEntryConsumer(Consumer<Map.Entry<String, String>> consumer) {
        cacheEntryConsumer(CACHE_CHANNEL, consumer);
    }

    /**
     * Cache some data
     * @param channel the name of the map
     * @param key the key
     * @param value the value
     */
    public static synchronized void cache(String channel, String key, String value) {
        final RMapCache<String, String> cache = client.getMapCache(channel);
        cache.put(key.toLowerCase(), value, 10, TimeUnit.MINUTES);
    }

    /**
     * Get the value from putting it
     * @param channel name of the map
     * @param key the key of the value
     * @return the value
     */
    public static String getCacheValue(String channel, String key) {
        final RMap<String, String> cache = client.getMapCache(channel);
        return cache.get(key.toLowerCase());
    }

    /**
     * Checks if the key exists
     * @param channel the name of the map
     * @param key - the unique identifier
     * @return if the key exists
     */
    public static boolean containsKey(String channel, String key) {
        final RMap<String, String> cache = client.getMapCache(channel);
        return cache.containsKey(key.toLowerCase());
    }

    /**
     * Remove key from the cache
     * @param channel the name of the map
     * @param key - the unique identifier
     */
    public static void removeCache(String channel, String key) {
        final RMap<String, String> cache = client.getMapCache(channel);
        cache.remove(key.toLowerCase());
    }
    // They are casted into arraylist because performance is slightly faster
    // when looping due to randomaccess interface
    /**
     * Get all the keys for a specific map
     * @param channel name of the map
     * @return
     */
    public static List<String> getCacheKeys(String channel) {
        final RMap<String, String> cache = client.getMapCache(channel);
        return new ArrayList<>(cache.keySet());
    }
    /**
     * Get all the values
     * @return the array list
     */
    public static List<String> getCacheValues(String channel) {
        final RMap<String, String> cache = client.getMapCache(channel);
        return new ArrayList<>(cache.values());
    }
    /**
     * Loop through all entries within the map
     * @param channel the name of the map
     * @param consumer the consumer to use. By using this, we avoid the creation of an array.
     */
    public static void cacheEntryConsumer(String channel, Consumer<Map.Entry<String, String>> consumer) {
        final RMap<String, String> cache = client.getMapCache(channel);
        for(Map.Entry<String, String> entry : new ArrayList<>(cache.entrySet())) {
            consumer.accept(entry);
        }
    }
    /**
     * You shouldn't have to call this method, but if
     * you need the native map object here
     * you go I guess
     * @param channel the name of the map
     * @return the map
     */
    public static Map<String, String> getCacheMap(String channel) {
        return client.getMapCache(channel);
    }


    /**
     * Give the pub sub listeners "ready" event
     */
    public static void readyGameLobby() {
        code = System.getProperty("lobby.code");
        if(code != null || code.isEmpty())
            controllerMessages.publish(code + " READY");
    }

    private static void listeners() {
        System.out.println("[Redis] Setting Listeners!");

    }

    public static String getCode() {
        return code;
    }

    public static RMap<String, String> getLobbyMap() {
        return client.getMap(code);
    }

    public static <Any> void putLobbyMap(Any key, Any value) {
        getLobbyMap().put(String.valueOf(key), String.valueOf(value));
    }

    public static <Any> Any getLobbykey(Any key) {
        return (Any) getLobbyMap().get(String.valueOf(key));
    }

    /**
     *
     * @param message publish any message to this server's pub sub
     */
    public static void publishLobby(String message) {
        controllerMessages.publish(message);
    }

    public static void shutdown() {
        client.shutdown();
    }
    private static String[] getCredentials() {
        final String HOST = System.getenv("REDIS_HOST");
        final String PASS = System.getenv("REDIS_PASS");
        if(HOST == null || PASS == null) {
            System.out.println(
                    "Failed to detect redis host and pass, stopping!\n" +
                    "Host: " + HOST + '\n' +
                    "Password: " + PASS);
        }
        return new String[]{HOST, PASS};
    }
}
