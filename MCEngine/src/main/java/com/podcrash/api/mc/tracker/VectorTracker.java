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

    private List<VectorCoordinate> getCoordinates(Player player) {
        return lastVectors.get(player.getName());
    }

    @Override
    public VectorCoordinate get(Player player) {
        List<VectorCoordinate> vectors = getCoordinates(player);
        return vectors == null || vectors.size() == 0 ? VectorCoordinate.zero() : vectors.get(vectors.size() - 1);
    }

    public VectorCoordinate getBefore(Player player, int time) {
        long timeInMS = System.currentTimeMillis() - time;
        List<VectorCoordinate> vectors = getCoordinates(player);

        for(int i = vectors.size() - 1; i >= 0; i--) {
            VectorCoordinate coordinate = vectors.get(i);
            long currentTime = coordinate.getTime();
            if(timeInMS > currentTime)
                return coordinate;
            if(timeInMS - 500L >= currentTime) break;
        }
        return null;
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
