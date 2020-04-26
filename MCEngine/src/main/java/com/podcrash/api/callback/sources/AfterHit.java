package com.podcrash.api.callback.sources;

import com.podcrash.api.callback.CallbackAction;

public class AfterHit extends CallbackAction<AfterHit> {
    private final long delayHit; // delay after getting hit
    private long activated;

    public AfterHit(long delay, long ticks, long actualdelay) {
        super(delay, ticks);
        this.activated = System.currentTimeMillis();
        this.delayHit = actualdelay;
        this.changeEvaluation(() -> currentTimeMillis() - this.activated >= this.delayHit);
    }
    public AfterHit(long delay) {
        this(1, 1, delay);
    }

    @Override
    public void run() {
        this.activated = System.currentTimeMillis();
        super.run();
    }

    @Override
    public void task() {
    }
}
