package com.podcrash.api.mc.util;

import com.podcrash.api.mc.location.BoundingBox;
import com.podcrash.api.mc.location.RayTracer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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

    /**
     * this check looks at the 2 vectors, the projectile's velocity and the vector of the projectile's position
     * and sees via ray tracing where it hits
     * @param expectedGrowth expand the hitbox by these directions (squared)
     * @param projVelo the velocity of the projectile
     * @param projLoc the location of the entity
     * @return Where the vector has hit, null if not
     */
    public static Vector projectile2DHit(double expectedGrowth, double distance, Vector projVelo, Vector projLoc,
                                    BoundingBox box) {
        //grow the box
        box = box.grow(expectedGrowth);

        RayTracer tracer = new RayTracer(projLoc, projVelo);
        //the accuracy by default is 0.8, there is no need to make it lower to have an extremely fine detection for hitboxes
        //that are basically 1 block wide
        return tracer.positionOfIntersection(box, distance, 0.95);
    }
}
