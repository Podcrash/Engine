package com.podcrash.api.mc.location;

import org.bukkit.util.Vector;

public class VectorCoordinate extends Coordinate {
    private final long time;
    public VectorCoordinate(double x, double y, double z, double yaw, double pitch, boolean onGround) {
        super(x, y, z, yaw, pitch, onGround);
        this.time = System.currentTimeMillis();
    }

    public VectorCoordinate(double x, double y, double z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, false);
    }

    public VectorCoordinate(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public VectorCoordinate(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public long getTime() {
        return time;
    }

    public static VectorCoordinate zero() {
        return new VectorCoordinate(0, 0,0 );
    }
}
