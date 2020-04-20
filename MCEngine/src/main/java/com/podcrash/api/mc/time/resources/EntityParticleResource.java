package com.podcrash.api.mc.time.resources;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.PacketType;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.util.RevealUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
            if(!canSee(this.entity, player)) continue;
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

    /**
     *
     * @param player - this is pretty misleading huh
     * @param viewer
     * @return
     */
    private boolean canSee(Entity player, Player viewer) {
        if(!(player instanceof Player)) return true;
        return viewer.canSee((Player) player);
    }
}