package com.podcrash.api.mc.effect.status.custom;

import com.podcrash.api.mc.effect.status.Status;
import org.bukkit.entity.Player;

public class BleedStatus extends CustomStatus {
    public BleedStatus(Player player) {
        super(player, Status.BLEED);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected boolean isInflicted() {
        return getApplier().has(Status.BLEED);
    }

    @Override
    protected void removeEffect() {
        getApplier().removeStatus(Status.BLEED);
    }
}
