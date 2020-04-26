package com.podcrash.api.events;

import com.podcrash.api.game.objects.ItemObjective;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ItemObjectiveSpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ItemObjective objective;

    public ItemObjectiveSpawnEvent(ItemObjective objective) {
        this.objective = objective;
    }

    public ItemObjective getObjective() {
        return objective;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
