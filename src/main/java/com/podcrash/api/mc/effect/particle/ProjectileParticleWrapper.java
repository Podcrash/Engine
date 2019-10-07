package com.podcrash.api.mc.effect.particle;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.List;

public class ProjectileParticleWrapper extends EntityParticleWrapper {
    public ProjectileParticleWrapper(Projectile entity, WrapperPlayServerWorldParticles packet, Player[] players) {
        super(entity, packet, null, players);
    }

    public ProjectileParticleWrapper(Projectile entity, WrapperPlayServerWorldParticles packet, List<Player> players) {
        super(entity, packet, null, players);
    }

    public ProjectileParticleWrapper(Projectile entity, WrapperPlayServerWorldParticles packet) {
        super(entity, packet, null);
    }

    @Override
    public boolean cancel(){
        return super.cancel() || getEntity().isOnGround();
    }
}
