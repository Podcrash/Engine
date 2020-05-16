package com.podcrash.api.time;

import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TimeHandler {
    private static final HashMap<TimeResource, List<Integer>> timeRunMap = new HashMap<>();
    private TimeHandler() {

    }

    public static void repeatedTime(long ticks, long delayTicks, TimeResource resource) {
        BukkitRunnable runnable = makeRunnable(resource);

        BukkitTask task = runnable.runTaskTimer(PodcrashSpigot.getInstance(), delayTicks, ticks);
        register(resource, task.getTaskId());
    }

    public static void repeatedTimeAsync(long ticks, long delayTicks, TimeResource resource) {
        BukkitRunnable runnable = makeRunnable(resource);

        BukkitTask task = runnable.runTaskTimerAsynchronously(PodcrashSpigot.getInstance(), delayTicks, ticks);
        register(resource, task.getTaskId());
    }

    public static void repeatedTimeSeconds(long seconds, long delaySeconds, TimeResource resource) {
        BukkitRunnable runnable = makeRunnable(resource);

        BukkitTask task = runnable.runTaskTimer(PodcrashSpigot.getInstance(), delaySeconds * 20, seconds * 20);
        register(resource, task.getTaskId());
    }

    public static void repeatedTimeSecondsAsync(long seconds, long delaySeconds, TimeResource resource) {
        BukkitRunnable runnable = makeRunnable(resource);

        BukkitTask task = runnable.runTaskTimerAsynchronously(PodcrashSpigot.getInstance(), delaySeconds * 20, seconds * 20);
        register(resource, task.getTaskId());
    }

    public static void delayTime(long delay, Runnable runnable) {
        BukkitRunnable run = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        run.runTaskLater(PodcrashSpigot.getInstance(), delay);
        //todo does this need to be registered?
    }
    public static void delayTime(long delay, TimeResource resource) {
        int taskID = new BukkitRunnable() {
            @Override
            public void run() {
                resource.task();
            }
        }.runTaskLater(PodcrashSpigot.getInstance(), delay).getTaskId();
        register(resource, taskID);
        //runnable.scheduleSync(plugin, delay * 20);
    }

    private static BukkitRunnable makeRunnable(TimeResource resource) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                resource.task();
                if (resource.cancel()) {
                    resource.cleanup();
                    unregister(resource);
                }
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
