package com.podcrash.api.mc.effect.status.custom;

import com.podcrash.api.mc.effect.status.Status;
import org.bukkit.entity.Player;

public class SilenceStatus extends CustomStatus {

    public SilenceStatus(Player player) {
        super(player, Status.SILENCE);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected void removeEffect() {
        getApplier().removeSilence();
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isSilenced();
    }
}
