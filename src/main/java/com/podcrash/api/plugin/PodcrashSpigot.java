package com.podcrash.api.plugin;

import com.podcrash.api.mc.tracker.CoordinateTracker;
import com.podcrash.api.mc.tracker.Tracker;
import com.podcrash.api.mc.tracker.VectorTracker;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Pluginizer.setInstance(this);
        enable();
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(this));
        addTracker(vectorTracker = new VectorTracker(this));
    }

    @Override
    public void onDisable() {
        for(Tracker tracker : trackers)
            tracker.disable();
        disable();
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
}
