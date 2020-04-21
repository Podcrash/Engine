package com.podcrash.api.mc.item;

/**
 * TODO
 * Will this be used..?
 */
public enum ItemRule {
    PICKUP_IGNORE(0),
    PICKUP_TARGET(1);

    private final int id;
    ItemRule(int id) {
        this.id = id;
    }
}
