package com.podcrash.api.mc.hologram;

import com.abstractpackets.packetwrapper.WrapperPlayServerEntityDestroy;
import com.abstractpackets.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.podcrash.api.mc.util.PacketUtil;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inspired from https://bukkit.org/threads/holograms.238097/
 */
public class Hologram {
    //TODO: use HologramMaker
    private static final double gapDistance = 0.23;
    private final List<String> lines;
    private final List<Integer> entityIDs = Collections.synchronizedList(new ArrayList<>());
    private Location location;
    private Vector pastLocation;
    private boolean distCheck = false;

    private boolean show;
    public Hologram(Location location, boolean distCheck, String... lines) {
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, lines);
        this.location = location;
        this.lines = strings;
        this.distCheck = distCheck;
        render();
    }
    public Hologram(Location location, String... lines) {
        this(location, false, lines);
    }
    public Hologram(Location location, boolean distCheck, List<String> lines) {
        this.location = location;
        this.lines = lines;
        this.distCheck = distCheck;
        render();
    }
    public Hologram(Location location, List<String> lines) {
        this(location, false, lines);
    }

    public List<String> getLines() {
        return this.lines;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isShowing() {
        return show;
    }

    public String readLine(int row) {
        return this.lines.get(row);
    }
    public void editLine(int row, String msg) {
        this.lines.set(row, msg);
    }

    /**
     * Spawn an invisible armor stand packet that will show a line at a specific location.
     * There is also a distance check to avoid being obnoxious.
     * @param line the string
     * @param loc where the line will be located
     * @return The armor stand id
     */
    private int showLine(String line, Location loc) {
        WrapperPlayServerSpawnEntityLiving living = spawnPacket(line, loc);
        for(Player player : loc.getWorld().getPlayers()) {
            if (distCheck && player.getLocation().distanceSquared(loc) <= 8D)
                continue;
            PacketUtil.syncSend(living, location.getWorld().getPlayers());
        }
        return living.getEntityID();
    }

    /**
     * Create a spawn packet
     * {@link Hologram#showLine(int, Location)}
     * @param line the line to use with the armor stand
     * @param loc where the line will be located
     * @return the armor stand packet
     */
    private WrapperPlayServerSpawnEntityLiving spawnPacket(String line, Location loc) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityArmorStand armorStand = new EntityArmorStand(worldServer);
        armorStand.setCustomName(line);
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        return new WrapperPlayServerSpawnEntityLiving(armorStand.getBukkitEntity());
    }

    private int showLine(int row, Location loc) {
        return showLine(this.lines.get(row), loc);
    }

    /**
     * If {@link Hologram#show} is true,
     * Using the current location, and for all the lines:
     * Spawn an invisible armor stand packet.
     * Subtract so that the words don't get mixed up.
     */
    public void render() {
        if (show)
            return;
        Location use = this.location.clone();
        final Vector down = new Vector(0, gapDistance, 0);
        for (String line : this.lines) {
            entityIDs.add(showLine(line, use));
            use.subtract(down);
        }
        pastLocation = use.toVector();
        show = true;

    }

    /**
     * Use a destroy packet to get rid of IDS
     */
    public void destroy() {
        if (!show || entityIDs.size() == 0) return;
        show = false;
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(entityIDs.stream().mapToInt(i -> i).toArray());
        PacketUtil.asyncSend(destroy, location.getWorld().getPlayers());
        this.entityIDs.clear();
    }

    /**
     * Combine the createScoreboard and destroy methods to seamlessly update values.
     */
    public void update() {
        //if the previous and current locations matchup, then you don't need to update it.
        if (pastLocation == location.toVector())
            return;
        destroy();
        render();
    }

    /**
     * @return the size of the lines
     */
    public int size(){
        return lines.size();
    }

    public boolean isDistCheck() {
        return distCheck;
    }
    public void setDistCheck(boolean distCheck) {
        this.distCheck = distCheck;
    }
}
