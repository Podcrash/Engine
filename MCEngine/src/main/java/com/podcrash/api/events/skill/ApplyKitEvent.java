package com.podcrash.api.events.skill;

import com.podcrash.api.kits.KitPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ApplyKitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private KitPlayer kitPlayer;
    private boolean keepInventory;
    private boolean cancel;

    public ApplyKitEvent(KitPlayer kitPlayer) {
        this.kitPlayer = kitPlayer;
        this.keepInventory = false;
    }

    public KitPlayer getKitPlayer() {
        return kitPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
