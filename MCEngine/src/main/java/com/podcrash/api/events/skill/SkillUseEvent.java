package com.podcrash.api.events.skill;

import com.podcrash.api.kits.Skill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;

public class SkillUseEvent extends SkillEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Action action;

    public SkillUseEvent(Skill skill) {
        super(skill);
        this.cancelled = false;
    }

    public SkillUseEvent(Skill skill, Action action) {
        super(skill);
        this.cancelled = false;
        this.action = action;
    }

    public Action getAction() {return action;}

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
