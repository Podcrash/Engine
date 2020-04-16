package com.podcrash.api.mc.effect.status.custom;

import com.abstractpackets.packetwrapper.WrapperPlayServerUpdateHealth;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Wrapper;

public class GroundStatus extends CustomStatus {
    public GroundStatus(Player player) {
        super(player, Status.GROUND);
        player.setFoodLevel(4);
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
        getPlayer().setFoodLevel(20);
        TimeHandler.delayTime(5, () -> getApplier().removeStatus(Status.JUMP_BOOST));
    }
}
