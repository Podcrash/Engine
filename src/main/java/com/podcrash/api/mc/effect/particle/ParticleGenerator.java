package com.podcrash.api.mc.effect.particle;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.util.VectorUtil;
import com.podcrash.api.mc.world.BlockUtil;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.List;

public final class ParticleGenerator {
    private ParticleGenerator() {

    }

    public static void generate(Player p, AbstractPacket... packets) {
        PacketUtil.syncSend(packets, p);
    }
    public static void generate(Player p, List<AbstractPacket> packets) {
        PacketUtil.syncSend(packets, p);
    }

    public static void sendToAll(Player player, AbstractPacket packet){
        PacketUtil.syncSend(packet, player);
    }

    public static void generateLocAs(final WrapperPlayServerWorldParticles packet, Location a, Location b) {
        Bukkit.getScheduler().runTaskAsynchronously(Pluginizer.getSpigotPlugin(), () -> {
            Vector vector = VectorUtil.fromAtoB(a, b).normalize();
            Location start = a.clone();
            List<Player> players = a.getWorld().getPlayers();
            while(BlockUtil.get2dDistanceSquared(start.toVector(), b.toVector()) > 1) {
                packet.setLocation(start);
                players.forEach(packet::sendPacket);
                start.add(vector);
            }
        });
    }

    public static WrapperPlayServerWorldEvent createPotionParticle(Location loc, int potionEffectId){
        WrapperPlayServerWorldEvent playServerWorldEvent = new WrapperPlayServerWorldEvent();
        playServerWorldEvent.setEffectId(2007);
        playServerWorldEvent.setLocation(new BlockPosition(loc.toVector()));
        playServerWorldEvent.setData(potionEffectId);
        playServerWorldEvent.setDisableRelativeVolume(false);
        return playServerWorldEvent;
    }

    public static WrapperPlayServerWorldEvent createBlockEffect(Location location, int blockID) {
        return createBlockEffect(location.toVector(), blockID);
    }
    public static WrapperPlayServerWorldEvent createBlockEffect(Vector vector, int blockID) {
        WrapperPlayServerWorldEvent event = new WrapperPlayServerWorldEvent();
        event.setLocation(new BlockPosition(vector));
        event.setEffectId(2001);
        event.setData(blockID);
        return event;
    }

    public static WrapperPlayServerWorldParticles createParticle(EnumWrappers.Particle particle, int count) {
       return createParticle(null, particle, count, 0,0,0);
    }

    public static WrapperPlayServerWorldParticles createParticle(Vector vector, EnumWrappers.Particle particle, int[] data, int particleCount, float offsetX, float offsetY, float offsetZ) {
        if(vector == null) vector = new Vector(0, 0,0);
        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
        packet.setParticleType(particle);
        packet.setX((float) vector.getX());
        packet.setY((float) vector.getY());
        packet.setZ((float) vector.getZ());
        packet.setNumberOfParticles(particleCount);
        packet.setOffsetX(offsetX);
        packet.setOffsetY(offsetY);
        packet.setOffsetZ(offsetZ);
        packet.setData(data);
        return packet;
    }
    public static WrapperPlayServerWorldParticles createParticle(Vector vector, EnumWrappers.Particle particle, int particleCount, float offsetX, float offsetY, float offsetZ) {
        return createParticle(vector, particle, new int[]{1}, particleCount, offsetX, offsetY, offsetZ);
    }

    public static void generateProjectile(Projectile proj, WrapperPlayServerWorldParticles packet) {
        ParticleRunnable.particleRunnable.getWrappers().add(new ProjectileParticleWrapper(proj, packet));
    }
    public static void generateEntity(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound){
        ParticleRunnable.particleRunnable.getWrappers().add(new EntityParticleWrapper(entity, packet, sound));
    }

    //for stuff like seismic slam
    public static void generateRangeParticles(final Location center, final double radius, final boolean under) {
        double radius2 = radius / 2d;
        double startX = center.getX() - radius2;
        double startZ = center.getZ() - radius2;
        double endX = center.getX() + radius2;
        double endZ = center.getZ() + radius2;
        List<Player> players = center.getWorld().getPlayers();
        for (double x = startX; x <= endX; x += 1D) {
            for (double z = startZ; z <= endZ; z += 1D) {
                Location test = BlockUtil.getHighestUnderneath(new Location(center.getWorld(), x, center.getY(), z));
                if(test.getBlock().getType() == Material.AIR) continue;
                AbstractPacket particle = createBlockEffect(test.toVector(), test.getBlock().getTypeId());
                PacketUtil.asyncSend(particle, players);

            }
        }
    }
}
