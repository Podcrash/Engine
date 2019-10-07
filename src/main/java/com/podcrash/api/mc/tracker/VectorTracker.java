package com.podcrash.api.mc.tracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;
import com.podcrash.api.mc.location.VectorCoordinate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VectorTracker implements IPlayerTrack<VectorCoordinate> {
    private JavaPlugin plugin;
    private PacketListener packetListener;
    private Map<String, List<VectorCoordinate>> lastVectors = new HashMap<>();
    @Override
    public VectorCoordinate get(Player player) {
        List<VectorCoordinate> vectors = lastVectors.get(player.getName());
        return vectors == null || vectors.size() == 0 ? new VectorCoordinate(player.getVelocity()) : vectors.get(vectors.size() - 1);
    }

    @Override
    public void enable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                packetListener = new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Server.ENTITY_VELOCITY) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        send(event);
                    }
                });
    }

    public void send(PacketEvent event) {
        StructureModifier<Integer> integers = event.getPacket().getIntegers();
        Entity entity = event.getPacket().getEntityModifier(event).read(0);
        if(!(entity instanceof Player)) return;
        double x = integers.read(0) / 8000.0D;
        double y = integers.read(1) / 8000.0D;
        double z = integers.read(2) / 8000.0D;
        add((Player) entity, new VectorCoordinate(x, y, z));
    }

    private void add(Player player, VectorCoordinate coordinate) {
        lastVectors.computeIfAbsent(player.getName(), k -> new ArrayList<>());
        List<VectorCoordinate> lastCoords = lastVectors.get(player.getName());
        lastCoords.add(coordinate);
        if(lastCoords.size() > 10)
            lastCoords.remove(0);
    }
    @Override
    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
    }

    public VectorTracker(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
