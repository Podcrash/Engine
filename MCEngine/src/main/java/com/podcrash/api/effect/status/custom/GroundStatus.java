package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.time.TimeHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GroundStatus extends CustomStatus {
    public GroundStatus(LivingEntity player) {
        super(player, Status.GROUND);
        if (instancePlayer)
            ((Player) player).setFoodLevel(4);
        StatusApplier.getOrNew(player).applyStatus(Status.JUMP_BOOST, 90, 128, true);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isGrounded();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeGround();
        if (instancePlayer)
            ((Player) getEntity()).setFoodLevel(20);
        TimeHandler.delayTime(5, () -> getApplier().removeStatus(Status.JUMP_BOOST));
    }
}
