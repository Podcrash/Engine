package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RootedStatus extends CustomStatus {
    private final PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 2, 128, true);

    public RootedStatus(LivingEntity player) {
        super(player, Status.ROOTED);
        if (instancePlayer) {
            ((Player) getEntity()).setFoodLevel(4);
            ((Player) getEntity()).setWalkSpeed(0);
        }
        StatusApplier.getOrNew(player).applyStatus(Status.JUMP_BOOST, 90, 128, true);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isRooted();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeRoot();
        getApplier().removeStatus(Status.JUMP_BOOST);

        if (instancePlayer) {
            ((Player) getEntity()).setFoodLevel(20);
            //0.2F is the default, if this needs to be changed, we'll see
            ((Player) getEntity()).setWalkSpeed(0.2F);
        }
    }
}
