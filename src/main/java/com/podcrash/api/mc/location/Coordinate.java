package com.podcrash.api.mc.location;

import com.podcrash.api.plugin.Pluginizer;
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
    private double x, y, z, yaw, pitch;
    private boolean ground;

    public static Coordinate fromLocation(Location location) {
        return new Coordinate(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    public static Coordinate fromVector(Vector vector) {
        return new Coordinate(vector.getX(), vector.getY(), vector.getZ());
    }
    public static Coordinate fromEntity(Entity entity) {
        if(!(entity instanceof Player)) return fromLocation(entity.getLocation());
        return Pluginizer.getSpigotPlugin().getCoordinateTracker().get((Player) entity, 1);
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

    public Vector toVector() {
        return new Vector(x, y, z);
    }
    public Location toLocation(World world) {
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
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

    /**
     * From Location#getDirection()
     * @return
     */
    public Vector getDirection() {
        Vector vector = new Vector();
        vector.setY(-Math.sin(Math.toRadians(pitch)));
        double xz = Math.cos(Math.toRadians(pitch));
        vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
        vector.setZ(xz * Math.cos(Math.toRadians(yaw)));

        return vector;
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
                '}';
    }

    /**
     * UTILITY METHODS
     */

    public void teleport(Entity entity, World world) {
        entity.teleport(new Location(world, x, y, z, (float) yaw, (float) pitch));
    }
}