package com.podcrash.api.mc.location;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class RayTracer {
    public static double DEFAULT_ESTIMATE = 0.12;
    //TODO: remove all the classes that use a bukkit Vector
    private Vector origin, direction;

    private Coordinate original, dir;

    @Deprecated
    public RayTracer(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
    /**
     * This smoothens it
     * @param box
     * @param distance
     * @param accuracy
     * @param soft
     * @return
     */
    public boolean intersectsBoundingBoxEstimate(BoundingBox box, double distance, double accuracy, double soft) {
        assert soft >= 0 && soft < 1;
        Coordinate mid = box.midPoint();
        Vector originDirection = direction.clone();
        Vector dirToMid = mid.subtract(origin).toVector().normalize();
        direction.add(dirToMid.multiply(soft)).normalize();
        boolean value = intersectsBoundingBox(box, distance, accuracy);
        direction = originDirection;
        return value;
    }
    public boolean intersectsBoundingBox(BoundingBox box, double distance, double accuracy) {
        assert accuracy > 0 && accuracy < 1;
        double add = 1D - accuracy;
        Vector lastV = null;
        for(double i = 0; i <= distance * 2; i += add) {
            Vector originClone = origin.clone();
            Vector addVector = direction.clone();

            originClone.add(addVector.multiply(i));
            lastV = originClone;
            if(intersectsBox(originClone, box)) {
                return lastV.distance(origin) < distance;
            }
        }
        return false;
    }
    public double intersectDistance(BoundingBox box, double maxDist, double accuracy, double soft) {
        assert soft >= 0 && soft < 1;
        assert accuracy > 0 && accuracy < 1;
        double add = 1D - accuracy;
        Vector originDirection = direction.clone();

        if(soft > 0) {
            Coordinate mid = box.midPoint();
            Vector dirToMid = mid.subtract(origin).toVector().normalize();
            originDirection.add(dirToMid.multiply(soft)).normalize();
        }
        for(double i = 0; i <= maxDist; i += add) {
            Vector originClone = origin.clone();
            Vector addVector = originDirection.clone();

            originClone.add(addVector.multiply(i));
            if(intersectsBox(originClone, box)) return i;
        }

        return -1;
    }

    public Vector positionOfIntersection(BoundingBox box, double distance, double accuracy) {
        assert accuracy > 0 && accuracy < 1;
        double add = 1D - accuracy;
        for(double i = 0; i <= distance; i += add) {
            Vector originClone = origin.clone();
            Vector addVector = direction.clone();

            originClone.add(addVector.multiply(i));
            if(intersectsBox(originClone, box) && originClone.distanceSquared(originClone) < distance * distance) return originClone;
        }
        return null;
    }

    public boolean intersectsBox(Vector vector, BoundingBox box) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        double minX = box.getA().getX();
        double minY = box.getA().getY();
        double minZ = box.getA().getZ();

        double maxX = box.getB().getX();
        double maxY = box.getB().getY();
        double maxZ = box.getB().getZ();

        if(compare(minX, x, maxX)) {
            if(compare(minY, y, maxY)) {
                if(compare(minZ, z, maxZ)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(double a, double b, double c) {
        return compare(a, b, c, DEFAULT_ESTIMATE);
    }

    /**
     * Find whether or not: b is in between a or c.
     * Estimate is the point of negligence.
     * @param a min
     * @param b value to test
     * @param c max
     * @param estimate offset
     * @return the
     */
    private boolean compare(double a, double b, double c, double estimate) {
        //First condition: keep old behavior
        //Second condition: allow some negligence
        return (a <= b && b <= c) || (a - estimate < b && b < c + estimate);
    }

    private void particles(World world, Vector vector) {
        world.playEffect(vector.toLocation(world), Effect.COLOURED_DUST, 0);
    }
}
