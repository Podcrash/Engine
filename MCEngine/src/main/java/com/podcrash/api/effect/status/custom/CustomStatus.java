package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.time.resources.TimeResource;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/*
    Base class for
 */
public abstract class CustomStatus implements TimeResource {
    protected final boolean instancePlayer;

    private final Status status;
    private final StatusApplier applier;
    private final LivingEntity entity;


    protected StatusApplier getApplier() {
        return applier;
    }

    protected LivingEntity getEntity() {
        return entity;
    }

    public String getName() {
        return status.getName();
    }

    public Status getStatus() {
        return status;
    }

    CustomStatus(LivingEntity entity, Status status) {
        if (status.isVanilla()) throw new IllegalArgumentException("Status cannot be vanilla");
        this.instancePlayer = entity instanceof Player;
        this.entity = entity;
        this.status = status;
        this.applier = StatusApplier.getOrNew(entity);
    }

    protected abstract void doWhileAffected();

    protected abstract boolean isInflicted();

    protected abstract void removeEffect();

    @Override
    public void task() {
        doWhileAffected();
    }

    @Override
    public boolean cancel() {
        if (isInflicted())
            return !(applier.getRemainingDuration(status) > 0);
        else
            return true;
    }

    @Override
    public void cleanup() {
        removeEffect();
        if (status != Status.INEPTITUDE && status != Status.SHOCK && status != Status.CLOAK)
            entity.sendMessage(String.format("%sCondition> %sYou have been cleared of %s.", ChatColor.BLUE, ChatColor.GRAY, status.getName()));
    }

    @Override
    public String toString(){
        return String.format("%s{%s:%.2f}", this.getClass().getSimpleName(), entity.getName(), applier.getRemainingDuration(status));
    }

}
