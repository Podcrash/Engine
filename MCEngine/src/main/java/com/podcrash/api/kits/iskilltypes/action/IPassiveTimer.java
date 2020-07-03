package com.podcrash.api.kits.iskilltypes.action;

import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;

public interface IPassiveTimer {
    void start();
    default void stop() {
        if (this instanceof TimeResource) TimeHandler.unregister((TimeResource) this);
    }
}
