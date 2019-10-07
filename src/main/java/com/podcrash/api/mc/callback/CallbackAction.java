package com.podcrash.api.mc.callback;

import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;

/**
 * Simple base class that can be explained be the following: {@link TimeResource}
 * while condition {@link CallbackAction#cancel()}
 * do a task {@link CallbackAction#task()}
 * else perform cleanup {@link CallbackAction#cleanup()}
 * @param <T> the callback action that will return.
 */
public abstract class CallbackAction<T extends CallbackAction> implements TimeResource {
    private long delay;
    private long ticks;
    private CallbackListener whileRun;
    private CallbackListener listener;
    private CallbackBoolean callbackBoolean;
    private boolean runOnce = true;

    /**
     * The constructor.
     * {@link TimeHandler#repeatedTime(long, long, TimeResource)}
     * @param delay the delay in milliseconds to start the callback
     * @param ticks how many synchronized ticks this should run
     */
    public CallbackAction(long delay, long ticks) {
        this.delay = delay;
        this.ticks = ticks;
        this.whileRun = () -> {};
    }
    public CallbackAction() {
        this(1, 1);
    }

    /**
     * Start this
     */
    public void run() {
        runOnce = true;
        TimeHandler.unregister(this);
        TimeHandler.repeatedTime(ticks, delay, this);
    }
    public void runAsync() {
        runOnce = true;
        TimeHandler.unregister(this);
        TimeHandler.repeatedTimeAsync(ticks, delay, this);
    }

    @SuppressWarnings("unchecked")
    public T doWhile(CallbackListener whileListener) {
        this.whileRun = whileListener;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T then(CallbackListener listener) {
        this.listener = listener;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T changeEvaluation(CallbackBoolean callbackBoolean) {
        this.callbackBoolean = callbackBoolean;
        return (T) this;
    }

    @Override
    public void task() {
        this.whileRun.run();
    }

    @Override
    public boolean cancel() {
        if (callbackBoolean == null) throw new NullPointerException("callBackBoolean is null");
        return this.callbackBoolean.evaluate();
    }

    @Override
    public void cleanup() {
        try {
            if (runOnce) {
                listener.run();
                runOnce = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
