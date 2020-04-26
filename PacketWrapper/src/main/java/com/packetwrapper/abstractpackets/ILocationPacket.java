package com.packetwrapper.abstractpackets;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * This class was made to make projectile packet wrappers easier
 */
public interface ILocationPacket {
    void setLocation(Location location);
    void setLocation(Vector vector);
    void setLocation(float x, float y, float z);

    default void setLocation(double x, double y, double z) {
        setLocation((float) x, (float) y, (float) z);
    }
}
