package com.podcrash.api.listeners;

import com.packetwrapper.abstractpackets.AbstractPacket;
import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.objects.action.ActionBlock;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.util.VectorUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ActionBlockListener extends ListenerBase {
    private static final Map<String, Long> cooldown = new HashMap<>();
    private static final Map<Vector, ActionVector> blocks = new LinkedHashMap<>();

    public ActionBlockListener(JavaPlugin plugin) {
        super(plugin);
    }

    public static void setBlocks(World world, List<ActionBlock> blocksL) {
        blocks.clear();
        for(ActionBlock block : blocksL) {
            ActionBlock.Type type = block.getType();
            Vector key = block.getVector1();
            Vector value = block.getVector2();
            switch (type) {
                case TELEPORT:
                    Vector up = new Vector(0, 1, 0);
                    addBlock(world, key, value.clone().add(up), type);
                    break;
                case SLIME:
                    addBlock(world, key, value, type);
                    break;
                default:
                    break;
            }
        }
    }

    private static void addBlock(World world, Vector key, Vector value, ActionBlock.Type type) {
        Location locationKey = key.toLocation(world);
        Vector newKey = new Vector(locationKey.getBlockX(), locationKey.getBlockY(), locationKey.getBlockZ());
        blocks.put(newKey, new ActionVector(value, type));
    }
    @EventHandler
    public void move(PlayerMoveEvent event) {
        Location location = event.getTo().getBlock().getLocation().subtract(0, 1, 0);
        Vector vector = location.toVector();
        ActionVector actionVector = blocks.get(vector);
        if (actionVector == null) {
            return;
        }
        long cd = cooldown.getOrDefault(event.getPlayer().getName(), -1L);
        if (cd >= System.currentTimeMillis())
            return;
        Player player = event.getPlayer();
        switch (actionVector.actionType) {
            case SLIME:
                player.setVelocity(actionVector.vector);
                FallDamageHandler.guaranteeSafeFall(event.getPlayer());
                break;
            case TELEPORT:
                Location teleportLoc = actionVector.vector.toLocation(location.getWorld());
                VectorUtil.conserveDirection(teleportLoc, player);

                //replace with an event
                if(!GameManager.getGame().isRespawning(player)) {
                    WrapperPlayServerWorldParticles startEffect = ParticleGenerator.createParticle(
                            player.getEyeLocation().toVector(), EnumWrappers.Particle.EXPLOSION_NORMAL, 5, 0, 0, 0);
                    WrapperPlayServerWorldParticles endEffect = ParticleGenerator.createParticle(
                            player.getEyeLocation().toVector(), EnumWrappers.Particle.EXPLOSION_NORMAL, 5, 0, 0, 0);

                    PacketUtil.syncSend(new AbstractPacket[] {startEffect, endEffect}, GameManager.getGame().getBukkitPlayers());

                    SoundPlayer.sendSound(player.getLocation(), "mob.endermen.portal", 1f, 63);
                    SoundPlayer.sendSound(player.getLocation(), "mob.endermen.portal", 1f, 63);
                }

                player.teleport(teleportLoc);
                break;
            default:
                return;
        }
        cooldown.put(event.getPlayer().getName(), System.currentTimeMillis() + 1500L);
    }

    private static class ActionVector {
        private final Vector vector;
        private final ActionBlock.Type actionType;

        public ActionVector(Vector vector, ActionBlock.Type actionType) {
            this.vector = vector;
            this.actionType = actionType;
        }
    }
}
