package com.podcrash.api.mc.time.resources;

import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;

public final class BlockBreakThenRestore {
    private long duration;
    private final Material material1;
    private final byte data;
    private final Location location;

    public BlockBreakThenRestore(int duration, Material material1, Location location, byte data) {
        this.duration = System.currentTimeMillis() + duration * 1000L;
        this.material1 = material1;
        this.location = location;
        this.data = data;
    }

    public BlockBreakThenRestore(int duration, Material material1, Location location) {
        this(duration, material1, location, (byte) 0);
    }

    public boolean check() {
        return System.currentTimeMillis() >= duration;
    }

    public void remove() {
        BlockUtil.setBlock(location, material1, data);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = System.currentTimeMillis() + duration * 1000L;
    }

    public Material getMaterial1() {
        return material1;
    }

    public Location getLocation() {
        return location;
    }
}
