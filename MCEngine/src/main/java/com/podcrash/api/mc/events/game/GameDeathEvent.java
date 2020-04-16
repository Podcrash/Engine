package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameDeathEvent extends GamePlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity killer;
    private boolean cancelled;
    private String causesMessage;

    public GameDeathEvent(Game game, Player who, LivingEntity killer, String message, String causesMessage) {
        super(game, who, message);
        this.killer = killer;
        this.causesMessage = causesMessage;
    }

    public GameDeathEvent(Game game, Player who, Player killer) {
        this(game, who, killer, String.format("Death> %s was killed by %s.", who.getName(), killer.getName()), null);
    }

    public LivingEntity getKiller() {
        return killer;
    }

    public String getDeathCausesMessage() {return causesMessage;}

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
