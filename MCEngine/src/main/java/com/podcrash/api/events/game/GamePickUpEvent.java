package com.podcrash.api.events.game;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.objects.ItemObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GamePickUpEvent extends GamePlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ItemObjective itemObjective;
    private boolean cancel = false;
    private final int remaining;

    public GamePickUpEvent(Game game, Player player, ItemObjective itemObjective, int remaining) {
        super(game, player, "You scored 300 points");
        this.itemObjective = itemObjective;
        this.remaining = remaining;
    }

    public ItemObjective getItem() {
        return this.itemObjective;
    }

    public int getRemaining() {
        return this.remaining;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
