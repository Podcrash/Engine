package com.podcrash.api.mc.location;

import com.podcrash.api.plugin.PodcrashSpigot;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Quite literally an immutable version of Vector + Location with other stuff.
 */
public class Coordinate {
    private final double x;
    private final double y;
    private final double z;
    private final double yaw;
    private final double pitch;
    private final boolean ground;

    public static Coordinate fromLocation(Location location) {
        return new Coordinate(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    public static Coordinate fromVector(Vector vector) {
        return new Coordinate(vector.getX(), vector.getY(), vector.getZ());
    }
    public static Coordinate fromEntity(Entity entity) {
        if (!(entity instanceof Player))
            return fromLocation(entity.getLocation());
        return PodcrashSpigot.getInstance().getCoordinateTracker().get((Player) entity, 1);
    }
    public static <K> Coordinate from(K inst) {
        if (inst instanceof Location)
            return fromLocation((Location) inst);
        else if (inst instanceof Vector)
            return fromVector((Vector) inst);
        else if (inst instanceof Entity)
            return fromEntity((Entity) inst);
        throw new IllegalArgumentException("inst must be either a Location, Vector, or Entity! Was a " + inst.getClass());
    }

    public Coordinate(double x, double y, double z, double yaw, double pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = onGround;
    }
    public Coordinate(double x, double y, double z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, false);
    }
    public Coordinate(double x, double y, double z) {
        this(x, y, z, 0, 0, false);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }
    public double getPitch() {
        return pitch;
    }
    public Coordinate setX(double x) {
        return new Coordinate(x, this.y, this.z, yaw, pitch);
    }
    public Coordinate setY(double y) {
        return new Coordinate(this.x, y, this.z, yaw, pitch);
    }
    public Coordinate setZ(double z) {
        return new Coordinate(this.x, this.y, z, yaw, pitch);
    }

    public Coordinate add(double x, double y, double z) {
        return new Coordinate(this.x + x, this.y + y, this.z + z, yaw, pitch, ground);
    }
    public Coordinate add(Coordinate coordinate) {
        return new Coordinate(this.x + coordinate.x, this.y + coordinate.y, this.z + coordinate.z, yaw, pitch);
    }
    public Coordinate add(Vector vector) {
        return new Coordinate(this.x + vector.getX(), this.y + vector.getY(), this.z + vector.getZ(), yaw, pitch);
    }
    public Coordinate subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }
    public Coordinate subtract(Coordinate coordinate) {
        return subtract(coordinate.x, coordinate.y, coordinate.z);
    }
    public Coordinate subtract(Vector vector) {
        return subtract(vector.getX(), vector.getY(), vector.getZ());
    }
    public Coordinate multiply(double multiplier) {
        return new Coordinate(x * multiplier, y * multiplier, z * multiplier, yaw, pitch);
    }
    public double dot(Coordinate coordinate) {
        return x * coordinate.x +
               y * coordinate.y +
               z * coordinate.z;
    }

    public double length() {
        return FastMath.sqrt(lengthSquared());
    }
    public double lengthSquared() {
        return x * x +
               y * y +
               z * z;
    }
    public Vector toVector() {
        return new Vector(x, y, z);
    }
    public Location toLocation(World world) {
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public Coordinate crossProduct(Coordinate coordinate) {
        double nx = y * coordinate.z - coordinate.y * z;
        double nz = y * coordinate.x - coordinate.y * x;
        double ny = x * coordinate.z - coordinate.x * z;

        return new Coordinate(nx, ny, nz);
    }
    public double distanceSquared(Coordinate coordinate) {
        double deltaX = coordinate.x - x;
        double deltaY = coordinate.y - y;
        double deltaZ = coordinate.z - z;
        return deltaX * deltaX +
               deltaY * deltaY +
               deltaZ * deltaZ;
    }
    public double distanceSquared() {
        return x * x + y * y + z * z;
    }
    public double distance() {
        return FastMath.sqrt(distanceSquared());
    }

    public boolean isGround() {
        return ground;
    }

    public Coordinate normalize() {
        double length = this.length();

        double newX = x/length;
        double newY = y/length;
        double newZ = z/length;

        return new Coordinate(newX, newY, newZ);
    }
    /**
     * From Location#getDirection()
     * @return
     */
    public Vector getDirection() {
        double pitchToRadians = Math.toRadians(pitch);
        double yawToRadians = Math.toRadians(yaw);

        Vector vector = new Vector();
        vector.setY(-Math.sin(pitchToRadians));
        double xz = Math.cos(pitchToRadians);
        vector.setX(-xz * Math.sin(yawToRadians));
        vector.setZ(xz * Math.cos(yawToRadians));

        return vector;
    }
    public Coordinate getDirectionFast() {
        double pitchToRadians = FastMath.toRadians(pitch);
        double yawToRadians = FastMath.toRadians(yaw);

        double y = -FastMath.sin(pitchToRadians);
        double xz = FastMath.cos(yawToRadians);

        double x = -xz * FastMath.sin(yawToRadians);
        double z = xz * FastMath.cos(yawToRadians);
        return new Coordinate(x, y, z);
    }

    /**
     * From {@link EntityLiving#setPosition(double, double, double)}
     * @param entity
     * @return
     */
    public BoundingBox getBoundingBox(Entity entity) {
        EntityLiving living = ((CraftLivingEntity) entity).getHandle();
        double f = living.width/ 2.0D;
        double f1 = living.length;
        return new BoundingBox(
                new Vector(x - f, y, z - f),
                new Vector(x + f, y + f1, z + f));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(x);
        result = 31 * result + Objects.hash(y, z);
        return result;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}' +
                '\n';
    }

    /**
     * UTILITY METHODS
     */
    public void teleport(Entity entity, World world) {
        entity.teleport(new Location(world, x, y, z, (float) yaw, (float) pitch));
    }
}