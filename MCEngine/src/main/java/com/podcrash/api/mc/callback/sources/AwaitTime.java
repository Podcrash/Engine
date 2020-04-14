package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;

/**
 * This class is used to await a certain amount of time
 */
public class AwaitTime extends CallbackAction<AwaitTime> {
    private long dueTime;

    public AwaitTime(long elapsedTimeMilles) {
        this.dueTime = System.currentTimeMillis() + elapsedTimeMilles;

        this.changeEvaluation( () -> System.currentTimeMillis() > dueTime);
    }
}
