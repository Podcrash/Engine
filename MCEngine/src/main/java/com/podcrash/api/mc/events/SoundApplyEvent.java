package com.podcrash.api.mc.events;

import com.podcrash.api.mc.sound.SoundWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @see com.podcrash.api.mc.damage.DamageQueue playSound(LivingEntity, LivingEntity, Cause)
 *
 */
public class SoundApplyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity victim;
    private final LivingEntity attacker;
    private SoundWrapper sound;
    private boolean cancel;

    public SoundApplyEvent(LivingEntity victim, LivingEntity attacker, SoundWrapper sound) {
        this.victim = victim;
        this.attacker = attacker;
        this.sound = sound;
        this.cancel = false;
    }

    public LivingEntity getVictim() {
        return victim;
    }
    public LivingEntity getAttacker() {
        return attacker;
    }
    public SoundWrapper getSound() {
        return sound;
    }

    public void setSound(SoundWrapper sound) {
        this.sound = sound;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
