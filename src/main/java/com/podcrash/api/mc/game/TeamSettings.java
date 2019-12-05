package com.podcrash.api.mc.game;

public final class TeamSettings {

    private int capacity;
    private int min;
    private int max;

    public TeamSettings() {
        this.capacity = 3;
    }

    public TeamSettings setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public TeamSettings setMin(int min) {
        this.min = min;
        return this;
    }

    public TeamSettings setMax(int max) {
        this.max = max;
        return this;
    }

}
