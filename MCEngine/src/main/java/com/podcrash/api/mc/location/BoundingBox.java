package com.podcrash.api.mc.location;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class BoundingBox {

    //min and max points of hit box
    private final Coordinate max;
    private final Coordinate min;

    private Coordinate[] allBounds;

    private static final Coordinate[] NORMALS;
    private final Set<Coordinate> normals = new HashSet<>();

    // is there a more intelligent way of doing this
    private static final int[][] planes = new int[][]{
        //xz
        {0, 2, 4},
        {6, 2, 7},
        {3, 1, 7},
        {5, 4, 1},
        //y
        {4, 6, 5},
        {3, 2, 1}
    };

    static {
        //TODO: Fill this out later since planes don't change at all.
        NORMALS = null;
    }

    public BoundingBox(Vector min, Vector max) {
        this.max = Coordinate.fromVector(max);
        this.min = Coordinate.fromVector(min);
    }

    public BoundingBox(Block block) {
        IBlockData blockData = ((CraftWorld) block.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        net.minecraft.server.v1_8_R3.Block blockNative = blockData.getBlock();
        blockNative.updateShape(((CraftWorld) block.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
        min = new Coordinate((double) block.getX() + blockNative.B(), (double) block.getY() + blockNative.D(), (double) block.getZ() + blockNative.F());
        max = new Coordinate((double) block.getX() + blockNative.C(), (double) block.getY() + blockNative.E(), (double) block.getZ() + blockNative.G());
    }

    public BoundingBox(Entity entity){
        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        min = new Coordinate(bb.a,bb.b,bb.c);
        max = new Coordinate(bb.d,bb.e,bb.f);
    }

    public BoundingBox (AxisAlignedBB bb){
        min = new Coordinate(bb.a,bb.b,bb.c);
        max = new Coordinate(bb.d,bb.e,bb.f);
    }

    public Coordinate midPoint(){
        return max.add(min).multiply(0.5);
    }

    public BoundingBox grow(double s) {
        return grow(s, s, s);
    }
    public BoundingBox grow(double x, double y, double z) {
        double ax = this.min.getX() - x;
        double ay = this.min.getY() - y;
        double az = this.min.getZ() - z;

        double bx = this.max.getX() + x;
        double by = this.max.getY() + y;
        double bz = this.max.getZ() + z;

        Vector newMin = new Vector(ax, ay, az);
        Vector newMax = new Vector(bx, by, bz);
        return new BoundingBox(newMin, newMax);
    }
    public String toString() {
        return String.format("box={%f-%f, %f=%f, %f-%f}", min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ());
    }

    /**
     * Calculates the distance between the bounding box and a given coordinate
     * @param p the origin-reference in this case
     * @return The distance between the coordinate and this bounding box
     */
    public double distance(@Nonnull Coordinate p) {
        if (min.getY() <= p.getY() && p.getY() <= max.getY()) {
            return hdistance(p);
        } else if (min.getZ() <= p.getZ() && p.getZ() <= max.getZ() &&
                 min.getX() <= p.getX() && p.getX() <= max.getX()) {
            return vdistance(p);
        } else {
            double hor = hdistance(p);
            double ver = vdistance(p);
            return Math.sqrt(hor * hor + ver * ver);
        }
    }

    /**
     * Finds the horizontal distance between this bounding box and a given coordinate
     * @param p The origin-reference
     * @return The horizontal distance between the coordinate and this bounding box
     */
    public double hdistance(Coordinate p) {
        double smallest = Double.MAX_VALUE;

        for(int i = 0; i < 4; i++) {
            int[] plane = planes[i];
            Coordinate normal = findNormal(plane[0], plane[1], plane[2]);
            normals.add(normal);
            Coordinate PQ = getBox()[plane[0]].subtract(p);
            double dist = Math.abs(PQ.dot(normal))/normal.length();
            if (1 < dist && dist < smallest) smallest = dist;
        }

        return smallest;
    }

    /**
     * Finds the vertical distance between this bounding box and a given coordinate
     * @param p The origin-reference
     * @return The vertical distance between the coordinate and this bounding box
     */
    public double vdistance(Coordinate p) {
        double smallest = Double.MAX_VALUE;

        for(int i = 4; i < 6; i++) {
            int[] plane = planes[i];
            Coordinate normal = findNormal(plane[0], plane[1], plane[2]);
            normals.add(normal);
            Coordinate PQ = getBox()[plane[0]].subtract(p);
            double dist = Math.abs(PQ.dot(normal))/normal.length();
            if (dist < smallest) smallest = dist;
        }

        return smallest;
    }

    /**
     * Finds the distance between this bounding box and another.
     * @param box The bounding box the calculate the distaance between.
     * @return The distance between the two
     */
    public double distance(BoundingBox box) {
        double min = Double.MAX_VALUE;
        for(Coordinate a : getBox()) {
            for(Coordinate b : box.getBox()) {
                double dist = a.distanceSquared(b);
                if (dist < min) {
                    min = dist;
                }
            }
        }
        return Math.sqrt(min);
    }

    /**
     * Gets the horizontal distance between this bounding box and another
     * @param box The bounding box to check
     * @return the distance between the two boxes
     */
    public double hdistance(BoundingBox box) {
        double min = Double.MAX_VALUE;
        for(Coordinate a : getBox()) {
            for(Coordinate b : box.getBox()) {
                double dist = a.setY(0).distanceSquared(b.setY(0));
                if (dist < min) {
                    min = dist;
                }
            }
        }
        return Math.sqrt(min);
    }

    public Coordinate getB() {
        return max;
    }

    public Coordinate getA() {
        return min;
    }

    /**
     * Debugs the boundingbox function by playing a particle where each of the 8 vertices are calculated to be.
     * @param world The world the box is in
     */
    public void debug(World world) {
        for(int i = 0; i < 8; i++) {
            debug(world, i);
        }
    }
    /**
     * Debugs the boundingbox function by playing a particle where a specified vertex is.
     * @param world The world the box is in
     * @param i index of the vertex to check.
     */
    public void debug(World world, int i) {
        world.playEffect(getBox()[i].toLocation(world), Effect.COLOURED_DUST, 0);
    }

    /**
     * Debugs the boundingbox function by playing a particle at a number of specified vertices.
     * @param world The world the box is in
     * @param i The array of vertex indices to check.
     */
    public void debug(World world, int[] i) {
        for(int d : i)
            debug(world, d);
    }

    /**
     * The numbers!
     *             5--------------7
     *           /|             /|
     *          / |            / |
     *         4--+-----------6  |
     *         |  |           |  |
     *         |  |           |  |
     *         |  |           |  |
     *         |  1-----------+--3
     *         | /            | /
     *         |/             |/
     *        0--------------2
     * @return the coordinates of the bounding box
     */
    public Coordinate[] getBox() {
        if (allBounds != null)
            return allBounds;

        double diffX = max.getX() - min.getX();
        double diffY = max.getY() - min.getY();
        double diffZ = max.getZ() - min.getZ();

        Coordinate minUp, minLeft, minRight,
                maxDown, maxLeft, maxRight;
        Coordinate up = new Coordinate(0, diffY, 0);
        Coordinate left = new Coordinate(diffX, 0, 0);
        Coordinate right = new Coordinate(0, 0, diffZ);

        minUp = min.add(up);
        minLeft = min.add(left);
        minRight = min.add(right);

        maxDown = max.subtract(up);
        maxLeft = max.subtract(left);
        maxRight = max.subtract(right);

        this.allBounds = new Coordinate[] {min, minLeft, minRight, maxDown, minUp, maxRight, maxLeft, max};
        return allBounds;
    }

    public Coordinate findNormal(int i1, int i2, int i3) {
        Coordinate[] box = getBox();
        return CoordinateHelper.findNormal(box[i1], box[i2], box[i3]);
    }


    public double[] findPlane(int i1, int i2, int i3) {
        Coordinate normalVector = findNormal(i1, i2, i3);
        return CoordinateHelper.findEquationPlane(getBox()[i1], normalVector);
    }


}