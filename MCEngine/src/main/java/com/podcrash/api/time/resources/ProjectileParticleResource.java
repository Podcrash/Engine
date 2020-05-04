package com.podcrash.api.time.resources;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import org.bukkit.entity.Projectile;

/**
 * Similar to EntityParticleResource, except with arrows.
 * See cancel()
 */
public class ProjectileParticleResource extends EntityParticleResource {

    public ProjectileParticleResource(Projectile proj, WrapperPlayServerWorldParticles packet) {
        super(proj, packet, null);
    }

    @Override
    public boolean cancel() {
        return super.cancel() || entity.isOnGround();
    }

}
