package com.podcrash.api.plugin;

import com.podcrash.api.mc.tracker.Tracker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class Pluginizer {
    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();
    private static PodcrashPlugin plugin;

    public static void setInstance(PodcrashPlugin plugin1) {
        if(plugin == null) plugin = plugin1;
        else throw new RuntimeException("This method should only be called once!");
    }

    public static void destroy() {
        plugin = null;
    }
    public static PodcrashPlugin getPlugin() {
        return plugin;
    }

    public static <K extends PodcrashPlugin> K getPlugin(Class<? extends K> pluginClasz) {
        return pluginClasz.cast(plugin);
    }

    public static PodcrashSpigot getSpigotPlugin() {
        return PodcrashSpigot.getInstance();
    }
    /**
     * Shorthand for {@link PodcrashSpigot#getTracker(Class)}
     * @param clasz
     * @param <T>
     * @return
     */
    public static <T extends Tracker> T getTracker(Class<? extends T> clasz) {
        return ((PodcrashSpigot) plugin).getTracker(clasz);
    }

    public static ExecutorService getService() {
        return SERVICE;
    }
    public static Logger getLogger() {
        return plugin.getLogger();
    }
}
