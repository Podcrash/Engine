package com.podcrash.api.events.skill;

import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// This event is called when a skill comes off cooldown.
public class SkillRechargeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private ICooldown ICooldown;
    private boolean canceled;
    private Player player;

    public SkillRechargeEvent(ICooldown skill, Player p) {
        ICooldown = skill;
        canceled = false;
        player= p;
    }

    public Player getPlayer() {
        return player;
    }

    public ICooldown getICooldown() {return ICooldown;}

    public String getSkillName() {return ICooldown.getName();}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }
}
