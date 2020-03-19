package com.podcrash.api.plugin;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.podcrash.api.db.tables.WorldLoader;
import com.podcrash.api.mc.Configurator;
import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.listeners.*;
import com.podcrash.api.mc.tracker.CoordinateTracker;
import com.podcrash.api.mc.tracker.Tracker;
import com.podcrash.api.mc.tracker.VectorTracker;
import com.podcrash.api.mc.world.WorldManager;
import com.podcrash.api.db.redis.Communicator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.redisson.api.RedissonClient;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PodcrashSpigot extends JavaPlugin implements PodcrashPlugin {
    private static PodcrashSpigot INSTANCE;
    public static PodcrashSpigot getInstance() {
        return INSTANCE;
    }
    private ExecutorService service = Executors.newCachedThreadPool();
    private int dQInt;

    private List<Tracker> trackers;
    private CoordinateTracker coordinateTracker;
    private VectorTracker vectorTracker;

    private final Map<String, Configurator> configurators = new HashMap<>();
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

    public void registerConfigurator(String identifier) {
        getLogger().info("Registering configurator: " + identifier);
        configurators.put(identifier, new Configurator(this, identifier));
    }

    /**
     * This method is used to start setting up for the game servers
     */
    public void gameStart() {
        getLogger().info("This Server is a game lobby with code" + Communicator.getCode());
        Future future = CompletableFuture.allOf(
                setKnockback(),
                registerListeners()
        );

        DamageQueue.active = true;
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DamageQueue(), 0, 0);
        dQInt = task.getTaskId();
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

    public void gameDisable() {
        for(World world : Bukkit.getWorlds()) {
            if(world.getName().equalsIgnoreCase("world")) continue;
            Bukkit.unloadWorld(world, false);
        }
        DamageQueue.active = false;
        Bukkit.getScheduler().cancelTask(dQInt);
        HandlerList.unregisterAll(this);
        for(Tracker tracker : trackers)
            tracker.disable();

    }
    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().info("Starting PodcrashSpigot!");
        Pluginizer.setInstance(this);

        Future future = CompletableFuture.allOf(
                enableWrap(),
                registerCommands());
        //WorldManager.getInstance().loadWorlds();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Communicator.readyGameLobby();
        if(Communicator.isGameLobby())
            gameStart();
    }

    @Override
    public void onDisable() {
        if(Communicator.isGameLobby())
            gameDisable();
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
            new PlayerInventoryListener(this);
            new StatusListener(this);
            new MobListeners(this);
            new ActionBlockListener(this);
            new TrapListener(this);
            // TODO: Add more listeners here..
        });
    }
    private CompletableFuture<Void> registerCommands() {
        return CompletableFuture.runAsync(() -> {

        });
    }

    public Configurator getConfigurator(String identifier) {
        return configurators.get(identifier);
    }
    public void reloadConfigurators() {
        CompletableFuture.runAsync(() ->
                configurators.values().forEach(Configurator::reloadConfig));
    }
}
