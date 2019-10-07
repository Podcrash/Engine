package com.podcrash.api.mc.effect.particle;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityParticleWrapper {
    private final Entity entity;
    private final WrapperPlayServerWorldParticles packet;
    private final SoundWrapper sound;
    private boolean cancel;
    private Player[] players;

    public EntityParticleWrapper(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound, List<Player> players) {
        this(entity, packet, sound, players.toArray(players.toArray(new Player[players.size()])));
    }
    public EntityParticleWrapper(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound) {
        this(entity, packet, sound, entity.getWorld().getPlayers().toArray(new Player[entity.getWorld().getPlayers().size()]));
    }
    public EntityParticleWrapper(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound, Player[] players) {
        this.entity = entity;
        this.packet = packet;
        this.sound = sound;
        this.cancel = false;
        this.players = players;
    }

    public Entity getEntity() {
        return entity;
    }

    public WrapperPlayServerWorldParticles getPacket() {
        return packet;
    }

    public SoundWrapper getSound() {
        return sound;
    }

    public void send() {
        packet.setLocation(entity.getLocation());
        PacketUtil.syncSend(packet, players);
        if(sound != null) SoundPlayer.sendSound(entity.getLocation(), sound.getSoundName(), sound.getVolume(), sound.getPitch());
    }


    public boolean cancel() {
        return !entity.isValid() || cancel;
    }

    public boolean isCancel() {
        return cancel;
    }
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
