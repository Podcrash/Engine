package com.podcrash.api.plugin;

import com.podcrash.api.commands.WorldCommand;
import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.listeners.*;
import com.podcrash.api.mc.tracker.CoordinateTracker;
import com.podcrash.api.mc.tracker.Tracker;
import com.podcrash.api.mc.tracker.VectorTracker;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PodcrashSpigot extends JavaPlugin implements PodcrashPlugin {
    private static PodcrashSpigot INSTANCE;
    public static PodcrashSpigot getInstance() {
        return INSTANCE;
    }
    private ExecutorService service = Executors.newCachedThreadPool();

    private List<Tracker> trackers;
    private CoordinateTracker coordinateTracker;
    private VectorTracker vectorTracker;

    @Override
    public void redis(RedissonClient client) {

    }

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }


    private void addTracker(Tracker tracker) {
        trackers.add(tracker);
        tracker.enable();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().info("Starting PodcrashSpigot!");
        Pluginizer.setInstance(this);
        Future future = CompletableFuture.allOf(
            enableWrap(),
            setKnockback(),
            registerListeners()
        );
        registerCommands();
        WorldManager.getInstance().loadWorlds();
        DamageQueue.active = true;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DamageQueue(), 0, 0);
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(this));
        addTracker(vectorTracker = new VectorTracker(this));

        try {
            getLogger().info("Awaiting....");
            future.get();
            getLogger().info("Awaited!");
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        for(Tracker tracker : trackers)
            tracker.disable();
        disable();
        WorldManager.getInstance().unloadWorlds();
    }

    @Override
    public void onLoad() {
    }

    public <K extends Tracker> K getTracker(Class<? extends K> trackerClasz) {
        for(Tracker tracker : trackers)
            if(tracker.getClass().equals(trackerClasz))
                return trackerClasz.cast(tracker);
        throw new RuntimeException("tracker is null, shouldn't happen");
    }
    public CoordinateTracker getCoordinateTracker() {
        return coordinateTracker;
    }
    public VectorTracker getVectorTracker() {
        return vectorTracker;
    }


    /**
     * Customized Knockback
     * @return
     */
    private CompletableFuture<Void> setKnockback() {
        return CompletableFuture.runAsync(() -> {
            Logger log = this.getLogger();
            log.info("Kb Numbers: ");
        /*

        getDouble("settings.knockback.friction", knockbackFriction);
        getDouble("settings.knockback.horizontal", knockbackHorizontal);
        getDouble("settings.knockback.vertical", knockbackVertical);
        getDouble("settings.knockback.verticallimit", knockbackVerticalLimit);
        getDouble("settings.knockback.extrahorizontal", knockbackExtraHorizontal);
        getDouble("settings.knockback.extravertical", knockbackExtraVertical);
         */

            SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
            SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
            SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
            SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
            SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
            SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");


            log.info("Friction: " + SpigotConfig.knockbackFriction);
            log.info("Horizontal: " + SpigotConfig.knockbackHorizontal);
            log.info("Veritcal: " + SpigotConfig.knockbackVertical);
            log.info("Vertical Limit: " + SpigotConfig.knockbackVerticalLimit);
            log.info("Extra Horizontal: " + SpigotConfig.knockbackExtraHorizontal);
            log.info("Extra Vertical: " + SpigotConfig.knockbackExtraVertical);

        }, getExecutorService());
    }
    private CompletableFuture<Void> registerListeners() {
        return CompletableFuture.runAsync(() -> {
            new GameListener(this);
            new GameDamagerConverterListener(this);
            new MapMaintainListener(this);
            new SpigotJoinListener(this);
            new StatusListener(this);
        });
    }
    private void registerCommands() {
        getCommand("pworld").setExecutor(new WorldCommand());
    }
}
