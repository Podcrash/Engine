package com.podcrash.api.mc.time;

import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TimeHandler {
    private static final HashMap<TimeResource, List<Integer>> timeRunMap = new HashMap<>();

    private TimeHandler() {

    }

    public static void repeatedTime(long ticks, long delayTicks, TimeResource resource) {
        Runnable runnable = makeRunnable(resource);

        //Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable).getTaskId();
        int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pluginizer.getSpigotPlugin(), runnable, delayTicks, ticks);
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void repeatedTimeAsync(long ticks, long delayTicks, TimeResource resource) {
        Runnable runnable = makeRunnable(resource);

        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Pluginizer.getSpigotPlugin(), runnable, delayTicks, ticks).getTaskId();
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void repeatedTimeSeconds(long seconds, long delaySeconds, TimeResource resource) {
        Runnable runnable = makeRunnable(resource);

        int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pluginizer.getSpigotPlugin(), runnable, delaySeconds * 20, seconds * 20);
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void repeatedTimeSecondsAsync(long seconds, long delaySeconds, TimeResource resource) {
        Runnable runnable = makeRunnable(resource);

        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Pluginizer.getSpigotPlugin(), runnable, delaySeconds * 20, seconds * 20).getTaskId();
        register(resource, taskID);
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
    }

    public static void delayTime(long delay, Runnable runnable) {
        int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Pluginizer.getSpigotPlugin(), runnable, delay);
        //todo does this need to be registered?
    }
    public static void delayTime(long delay, TimeResource resource) {
        int taskID = new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskLater(Pluginizer.getSpigotPlugin(), delay).getTaskId();
        register(resource, taskID);
        //runnable.scheduleSync(plugin, delay * 20);
    }

    private static Runnable makeRunnable(TimeResource resource) {
        return () -> {
            resource.task();
            if (resource.cancel()) {
                unregister(resource);
                resource.cleanup();
            }
        };
    }

    private static void register(TimeResource resource, int taskID) {
        if (timeRunMap.get(resource) == null) {
            List<Integer> newList = new ArrayList<>();
            newList.add(taskID);
            timeRunMap.put(resource, newList);
        } else {
            timeRunMap.get(resource).add(taskID);
        }
    }

    public static void unregister(TimeResource resource) {
        if (timeRunMap.containsKey(resource)) {
            timeRunMap.get(resource).forEach((taskID) -> Bukkit.getScheduler().cancelTask(taskID));
            timeRunMap.remove(resource, timeRunMap.get(resource));
        }
    }

    public static void forceDestroy(TimeResource resource) {
        if (timeRunMap.containsKey(resource))
            timeRunMap.remove(resource, timeRunMap.get(resource));
        timeRunMap.get(resource).forEach((taskID) -> Bukkit.getScheduler().cancelTask(taskID));
    }

    public static HashMap<TimeResource, List<Integer>> getTimeRunMap() {
        return timeRunMap;
    }

}
