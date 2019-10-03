package com.podcrash.api.redis;

import org.bukkit.Bukkit;
import org.omg.CORBA.Any;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Communicator {
    private static RedissonClient client;
    private static String code;
    private static RTopic controllerMessages;

    private static boolean bukkitFound;

    public static CompletableFuture<Void> setup(Executor executor) {
        bukkitFound = findBukkit();

        System.out.println("[Redis] Starting!");
        Config config = new Config();
        String[] creds = getCredentials();
        System.out.println("[Redis] Credentials: " + creds[0] + " " + creds[1]);
        config.useSingleServer()
                .setAddress(creds[0])
                .setPassword(creds[1])
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);

        client = Redisson.create(config);
        code = System.getProperty("lobby.code");
        System.out.println("[Redis] This lobby's code: " + code);
        controllerMessages = client.getTopic("controller-messages");

        ready();
        listeners();
        return CompletableFuture.runAsync(() -> {
            try {

            }catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);
    }

    private static void ready() {
        controllerMessages.publish(code + " READY");
        if(!bukkitFound) return;

        System.out.println("[Redis] Ready! test: " + getMap().get("maxsize"));
    }
    private static void listeners() {

    }

    public static String getCode() {
        return code;
    }

    public static RMap<String, String> getMap() {
        return client.getMap(code);
    }

    public static <Any> void put(Any key, Any value) {
        getMap().put(String.valueOf(key), String.valueOf(value));
    }

    public static <Any> Any get(Any key) {
        return (Any) getMap().get(String.valueOf(key));
    }

    public static void publish(String message) {
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
            if(bukkitFound)
                Bukkit.shutdown();
        }
        return new String[]{HOST, PASS};
    }

    private static boolean findBukkit() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
