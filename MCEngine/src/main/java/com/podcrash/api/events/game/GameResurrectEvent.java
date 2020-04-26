package com.podcrash.api.events.game;

import com.podcrash.api.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameResurrectEvent extends GamePlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public GameResurrectEvent(Game game, Player who) {
        super(game, who, String.format("%sRespawn>%s You are now visible.", ChatColor.BLUE, ChatColor.GRAY));
    }

    public GameResurrectEvent(Game game, Player who, String message) {
        super(game, who, message);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
