package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;

public class AfterHit extends CallbackAction<AfterHit> {
    private long delayHit; // delay after getting hit
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
