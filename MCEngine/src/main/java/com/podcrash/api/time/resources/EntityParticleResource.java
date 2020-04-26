package com.podcrash.api.time.resources;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.sound.SoundWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Give players particles as used in {@link WrapperPlayServerWorldParticles}
 */
public class EntityParticleResource implements TimeResource {
    protected Entity entity;
    private final WrapperPlayServerWorldParticles packet;
    private final SoundWrapper sound;
    public boolean cancel = false;

    public EntityParticleResource(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound) {
        this.entity = entity;
        this.packet = packet;
        this.sound = sound;
    }

    @Override
    public void task() {
        packet.setLocation(entity.getLocation());
        for(Player player : entity.getWorld().getPlayers()) {
            if (!canSee(this.entity, player))
                continue;
            packet.sendPacket(player);
        }
        if (sound != null)
            SoundPlayer.sendSound(entity.getLocation(), sound.getSoundName(), sound.getVolume(), sound.getPitch());
    }

    @Override
    public boolean cancel() {
        return cancel || !entity.isValid();
    }

    @Override
    public void cleanup() {

    }

    /**
     * Checks if a given player can see another
     * @param toSee The entity being seen
     * @param viewer The player looking
     * @return If the viewer can see toSee or not.
     */
    private boolean canSee(Entity toSee, Player viewer) {
        if (!(toSee instanceof Player))
            return true;
        return viewer.canSee((Player) toSee);
    }
}