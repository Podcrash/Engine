package com.podcrash.api.redis;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class Communicator {
    private static RedissonClient client;
    private static String code;
    private static RTopic controllerMessages;

    public static CompletableFuture<Void> setup(Executor executor) {
        return setup(executor, (client) -> {});
    }
    public static CompletableFuture<Void> setup(Executor executor, Consumer<RedissonClient> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
            }catch(Exception e) {
                e.printStackTrace();
            }
            System.out.println("[Redis] Ready!");
            return client;
        }, executor).thenAcceptAsync(consumer, executor);
    }


    public static void readyGameLobby() {
        code = System.getProperty("lobby.code");
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
