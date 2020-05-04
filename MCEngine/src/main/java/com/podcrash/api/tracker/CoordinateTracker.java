package com.podcrash.api.tracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import com.podcrash.api.location.Coordinate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * The class is needed because it needs to get the most accurate position possible
 * by the player.
 */
public final class CoordinateTracker implements IPlayerTrack<Coordinate> {
    private final JavaPlugin plugin;
    private static final List<PacketType> PACKET_TYPES = Arrays.asList(
            PacketType.Play.Client.FLYING,
            PacketType.Play.Client.POSITION_LOOK,
            PacketType.Play.Client.POSITION,
            PacketType.Play.Client.LOOK,
            PacketType.Play.Client.USE_ENTITY);

    private PacketListener listener;
    private final Map<String, List<TimeCoordinate>> lastTimes;
    private final Map<String, Long> lastUsePackets;

    public CoordinateTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lastTimes = new HashMap<>();
        this.lastUsePackets = new HashMap<>();
    }

    @Override
    public void enable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                listener = new PacketAdapter(plugin, ListenerPriority.LOWEST, PACKET_TYPES) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                receive(event);
            }
        });
    }
    @Override
    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
    }

    @Override
    public Coordinate get(Player player) {
        return get(player.getName());
    }
    public Coordinate get(Player player, int index) {
        return get(player.getName(), index);
    }
    /**
     * Just accounts for the head height
     * @param player The player whose head to get
     * @return The coordinate of the player's head
     */
    public Coordinate getHead(Player player) {
        return get(player).add(0, player.getEyeHeight(), 0);
    }

    public Coordinate getHead(Player player, int index) {
        return get(player, index).add(0, player.getEyeHeight(), 0);
    }

    public Coordinate getTimeBefore(Player player, long timeInMS, boolean includeHead) {
        if (timeInMS > 0) timeInMS = 1;
        long timeInNS = timeInMS * 1000000;
        Coordinate poss = null;
        List<TimeCoordinate> coordinates = lastTimes.get(player.getName());
        long delta = System.nanoTime() - timeInNS;
        for(int i = coordinates.size() - 1; i >= 0; i--) {
            TimeCoordinate coordinate = coordinates.get(i);
            if (delta > coordinate.time) {
                poss = coordinate.coordinate;
                break;
            }
        }
        if (poss == null)
            return null;
        return includeHead ? poss.add(0, player.getEyeHeight(), 0) : poss;
    }

    public Coordinate getBeforeUsePacket(Player player) {
        return getBeforeUsePacket(player, false);
    }
    public Coordinate getBeforeUsePacket(Player player, boolean includeHead) {
        String playerName = player.getName();
        List<TimeCoordinate> coordinates = lastTimes.get(playerName);
        long lastUseTime = lastUsePackets.get(playerName) - 18000000;
        long delta = -1;
        //realistically, nullpointerexceptions should never happen
        //because the time it takes for players to hit someone
        //immediately after it starts tracking is unrealistic.
        Coordinate coordinate = null;
        for(int i = coordinates.size() - 1; i >= 0; i--) {
            TimeCoordinate poss = coordinates.get(i);
            if (lastUseTime > poss.time) {
                delta = lastUseTime - poss.time;
                coordinate = poss.coordinate;
                break;
            }
        }
        if (coordinate == null)
            return null;
        return includeHead ? coordinate.add(0, player.getEyeHeight(), 0) : coordinate;
    }
    /**
     * Finds the last position updated by the player.
     * @param name the player name
     * @return the last coordinate the player was in.
     */
    private Coordinate get(String name) {
        return get(name, 0);
    }
    private Coordinate get(String name, int indexFromLast) {
        int last = lastTimes.get(name).size() - 1 - indexFromLast;
        return lastTimes.get(name).get(last).coordinate;
    }
    private void receive(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) {
            recordLocation(event.getPlayer(), event.getPacket());
        } else {
            Entity target = event.getPacket().getEntityModifier(event).read(0);
            if (target instanceof Player)
                lastUsePackets.put(target.getName(), System.nanoTime());
        }
    }

    /**
     * Finds the current position from the position packets and stores them.
     * This used 4 packets because sometimes the player doesn't:
     * a) move
     * b) aim
     * c) or both of the above at the same time.
     *
     * Flying covers not moving and not aiming
     * Position-look covers both moving and aiming
     * Position covers moving and not aiming
     * Look covers not moving and aiming
     */
    private void recordLocation(Player player, PacketContainer packet) {
        double x = 0, y = 0, z = 0;
        float yaw = 0, pitch = 0;

        boolean ground;
        if (packet.getType() == PacketType.Play.Client.FLYING) {
            Location location = player.getLocation();
            x = location.getX();
            y = location.getY();
            z = location.getZ();

            yaw = location.getYaw();
            pitch = location.getPitch();

            ground = true;
        } else {
            ground = packet.getBooleans().read(0);
            if (packet.getType() == PacketType.Play.Client.POSITION_LOOK) {
                StructureModifier<Double> doubles = packet.getDoubles();
                x = doubles.read(0);
                y = doubles.read(1);
                z = doubles.read(2);

                StructureModifier<Float> floats = packet.getFloat();
                yaw = floats.read(0);
                pitch = floats.read(1);

            } else {
                Location location = player.getLocation();
                if (packet.getType() == PacketType.Play.Client.POSITION) {
                    StructureModifier<Double> doubles = packet.getDoubles();
                    x = doubles.read(0);
                    y = doubles.read(1);
                    z = doubles.read(2);

                    yaw = location.getYaw();
                    pitch = location.getPitch();

                } else if (packet.getType() == PacketType.Play.Client.LOOK) {
                    x = location.getX();
                    y = location.getY();
                    z = location.getZ();

                    StructureModifier<Float> floats = packet.getFloat();
                    yaw = floats.read(0);
                    pitch = floats.read(1);
                }
            }
        }

        //1.62 accounts for the head
        //old comment ^ just use the normal head height method so that sneaking
        //doesn't make false positives
        //y += player.getEyeHeight();
        Coordinate newUpdate = new Coordinate(x, y, z, yaw, pitch, ground);
        add(player, newUpdate);
    }

    /**
     * Only store the last 20 locations
     * @param player the player to add to the map
     * @param coordinate the coordinate to add
     */
    private void add(Player player, Coordinate coordinate) {
        lastTimes.computeIfAbsent(player.getName(), k -> new ArrayList<>());
        List<TimeCoordinate> lastCoords = lastTimes.get(player.getName());
        lastCoords.add(new TimeCoordinate(System.nanoTime(), coordinate));
        if (lastCoords.size() > 20)
            lastCoords.remove(0);
    }

    private static class TimeCoordinate  {
        private final long time;
        private final Coordinate coordinate;

        private TimeCoordinate(long time, Coordinate coordinate) {
            this.time = time;
            this.coordinate = coordinate;
        }
    }
}
