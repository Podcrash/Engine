package com.podcrash.api.kits.iskilltypes.action;

public interface IPassiveTimer {
    void start();
    default void stop() {

    }
}
