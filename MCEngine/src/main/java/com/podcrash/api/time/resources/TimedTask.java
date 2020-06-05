package com.podcrash.api.time.resources;

import com.podcrash.api.plugin.PodcrashSpigot;
import net.jafama.FastMath;
import org.bukkit.Bukkit;

public abstract class TimedTask implements TimeResource {
    private long timeStarted;
    private long duration;
    private boolean isAsync = true;

    public TimedTask(long duration) {
        this.duration = duration;

    }

    /**
     * if it uses the bukkit API, setAsync(false)
     * @param async
     */
    public void setAsync(boolean async) {
        isAsync = async;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public void setDuration(long duration) {
        this.duration = FastMath.abs(duration);
    }

    public abstract void action();

    @Override
    public void task() {
        if (System.currentTimeMillis() - timeStarted > duration) {
            if (!isAsync) Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), this::action);
            else action();
            timeStarted = System.currentTimeMillis();
        }
    }

    public void start() {
        this.timeStarted = System.currentTimeMillis();
        runAsync(5, 0);
    }

    @Override
    public boolean cancel() {
        return false;
    }


    // assume that we aren't going to do anything else, end user can do something else however so if it needs to be overriden, go ahead
    @Override
    public void cleanup() {

    }
}
