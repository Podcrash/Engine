package com.podcrash.api.effect.status.custom;

import com.packetwrapper.abstractpackets.AbstractPacket;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.util.PacketUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BleedStatus extends CustomStatus {
    private List<Player> players;
    private int i = 0;
    public BleedStatus(LivingEntity player) {
        super(player, Status.BLEED);
        this.players = getEntity().getWorld().getPlayers();
    }

    @Override
    protected void doWhileAffected() {
        if (i >= 10) {
            AbstractPacket bleedPacket = ParticleGenerator.createBlockEffect(getEntity().getLocation(), Material.REDSTONE_BLOCK.getId());
            PacketUtil.asyncSend(bleedPacket, players);
            i = 0;
        }
        i++;
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().has(Status.BLEED);
    }

    @Override
    protected void removeEffect() {
        getApplier().removeStatus(Status.BLEED);
        players.clear();
        players = null;
    }
}
