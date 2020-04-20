package com.podcrash.api.mc.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public final class VectorUtil {
    private VectorUtil() {

    }

    public static Vector fromAtoB(Location a, Location b) {
        return fromAtoB(a.toVector(), b.toVector());
    }

    public static Vector fromAtoB(Vector a, Vector b) {
        return b.subtract(a);
    }

    /**
     *
     * @param a
     * @param b
     * @param degreesBased180 (0, 360]
     * @return
     */
    public static boolean angleIsAround(Location a, Location b, double degreesBased180) {
        return angleIsAround(a.toVector(), b.toVector(), degreesBased180);
    }

    public static boolean angleIsAround(Vector a, Vector b, double degreesBased180) {
        a = a.clone().normalize().setY(0);
        b = b.clone().normalize().setY(0);
        float angle = a.angle(b);
        return Math.toDegrees(angle) <= degreesBased180;
    }

    public static void conserveDirection(Location location, Entity entity) {
        location.setDirection(entity.getLocation().getDirection());
    }
}
