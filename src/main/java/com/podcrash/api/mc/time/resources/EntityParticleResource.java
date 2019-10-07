package com.podcrash.api.mc.time.resources;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Give players particles as used in {@link WrapperPlayServerWorldParticles}
 */
public class EntityParticleResource implements TimeResource {
    protected Entity entity;
    private WrapperPlayServerWorldParticles packet;
    private SoundWrapper sound;
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
            if(this.entity instanceof Player) {
                if(!player.canSee((Player) this.entity)) continue;
            }
            packet.sendPacket(player);
        }
        if(sound != null) SoundPlayer.sendSound(entity.getLocation(), sound.getSoundName(), sound.getVolume(), sound.getPitch());
    }

    @Override
    public boolean cancel() {
        return cancel || !entity.isValid();
    }

    @Override
    public void cleanup() {

    }
}