package com.podcrash.api.mc.location;

import org.bukkit.util.Vector;

public class VectorCoordinate extends Coordinate {
    public VectorCoordinate(double x, double y, double z, double yaw, double pitch, boolean onGround) {
        super(x, y, z, yaw, pitch, onGround);
    }

    public VectorCoordinate(double x, double y, double z, double yaw, double pitch) {
        super(x, y, z, yaw, pitch);
    }

    public VectorCoordinate(double x, double y, double z) {
        super(x, y, z);
    }

    public VectorCoordinate(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }
}
