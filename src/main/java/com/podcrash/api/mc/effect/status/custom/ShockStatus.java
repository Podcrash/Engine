package com.podcrash.api.mc.effect.status.custom;

import com.podcrash.api.mc.effect.status.Status;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

public class ShockStatus extends CustomStatus {
    public ShockStatus(Player player) {
        super(player, Status.SHOCK);
    }

    @Override
    protected void doWhileAffected() {
        getPlayer().playEffect(EntityEffect.HURT);
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
