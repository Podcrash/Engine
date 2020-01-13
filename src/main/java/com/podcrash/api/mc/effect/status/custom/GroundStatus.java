package com.podcrash.api.mc.effect.status.custom;

import com.abstractpackets.packetwrapper.WrapperPlayServerUpdateHealth;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Wrapper;

public class GroundStatus extends CustomStatus {
    private final PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true);
    public GroundStatus(Player player) {
        super(player, Status.GROUND);
        player.setFoodLevel(4);
    }

    @Override
    protected void doWhileAffected() {
        new TimeResource() {
            @Override
            public void task() {
                if (!getPlayer().hasPotionEffect(PotionEffectType.JUMP)) getPlayer().addPotionEffect(jump);
            }

            @Override
            public boolean cancel() {
                return true;
            }

            @Override
            public void cleanup() {

            }
        }.delaySync(1);
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
