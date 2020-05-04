package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ShockStatus extends CustomStatus {
    public ShockStatus(LivingEntity player) {
        super(player, Status.SHOCK);
    }

    @Override
    protected void doWhileAffected() {
        getEntity().playEffect(EntityEffect.HURT);
    }

    @Override
    protected void removeEffect() {
        getApplier().removeShock();
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isShocked();
    }
}
