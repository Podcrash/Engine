package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MarkedStatus extends CustomStatus {

    public MarkedStatus(LivingEntity player) {
        super(player, Status.MARKED);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected void removeEffect() {
        getApplier().removeMark();
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isMarked();
    }
}
