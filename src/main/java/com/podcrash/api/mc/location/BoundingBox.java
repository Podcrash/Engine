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

public class BoundingBox {

    //min and max points of hit box
    Vector max;
    Vector min;

    public BoundingBox(Vector min, Vector max) {
        this.max = max;
        this.min = min;
    }

    public BoundingBox(Block block) {
        IBlockData blockData = ((CraftWorld) block.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        net.minecraft.server.v1_8_R3.Block blockNative = blockData.getBlock();
        blockNative.updateShape(((CraftWorld) block.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
        min = new Vector((double) block.getX() + blockNative.B(), (double) block.getY() + blockNative.D(), (double) block.getZ() + blockNative.F());
        max = new Vector((double) block.getX() + blockNative.C(), (double) block.getY() + blockNative.E(), (double) block.getZ() + blockNative.G());
    }

    public BoundingBox(Entity entity){
        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        min = new Vector(bb.a,bb.b,bb.c);
        max = new Vector(bb.d,bb.e,bb.f);
    }

    public BoundingBox (AxisAlignedBB bb){
        min = new Vector(bb.a,bb.b,bb.c);
        max = new Vector(bb.d,bb.e,bb.f);
    }

    public Vector midPoint(){
        return max.clone().add(min).multiply(0.5);
    }

    public String toString() {
        return String.format("box={%f-%f, %f=%f, %f-%f}", min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ());
    }

    public double distance(BoundingBox box) {
        double min = Double.MAX_VALUE;
        for(Vector a : getBox()) {
            for(Vector b : box.getBox()) {
                double dist = a.distanceSquared(b);
                if(dist < min) {
                    min = dist;
                }
            }
        }
        return Math.sqrt(min);
    }

    public double distanceHorizontal(BoundingBox box) {
        double min = Double.MAX_VALUE;
        for(Vector a : getBox()) {
            for(Vector b : box.getBox()) {
                double dist = a.setY(0).distanceSquared(b.setY(0));
                if(dist < min) {
                    min = dist;
                }
            }
        }
        return Math.sqrt(min);
    }

    public void debug(World world) {
        for(Vector vector : getBox()) {
            world.playEffect(vector.toLocation(world), Effect.COLOURED_DUST, 0);
        }
    }

    public Vector[] getBox() {
        double diffX = max.getX() - min.getX();
        double diffY = max.getY() - min.getY();
        double diffZ = max.getZ() - min.getZ();

        Vector minUp, minLeft, minRight,
                maxDown, maxLeft, maxRight;
        Vector up = new Vector(0, diffY, 0);
        Vector left = new Vector(diffX, 0, 0);
        Vector right = new Vector(0, 0, diffZ);

        minUp = min.clone().add(up);
        minLeft = min.clone().add(left);
        minRight = min.clone().add(right);

        maxDown = max.clone().subtract(up);
        maxLeft = max.clone().subtract(left);
        maxRight = max.clone().subtract(right);

        return new Vector[] {min.clone(), minLeft, minRight, maxDown, minUp, maxRight, maxLeft, max.clone()};
    }

}