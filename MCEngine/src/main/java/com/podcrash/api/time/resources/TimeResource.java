package com.podcrash.api.time.resources;


import com.podcrash.api.time.TimeHandler;

public interface TimeResource {

    void task();

    boolean cancel(); //condition for canceling early

    void cleanup();

    /*
     * Wrapper for calling the millisecond method for time dependent results
     */
    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    default void run(long ticks, long delay) {
        TimeHandler.repeatedTime(ticks, delay, this);
    }
    default void run(long ticks) {
        this.run(ticks, 0);
    }

    default void delaySync(long delay) {
        TimeHandler.delayTime(delay, this);
    }

    default void runAsync(long ticks, long delay) {
        TimeHandler.repeatedTimeAsync(ticks, delay, this);
    }

    default void unregister(){
        TimeHandler.unregister(this);
    }
}
