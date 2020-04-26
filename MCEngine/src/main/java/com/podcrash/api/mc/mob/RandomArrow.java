package com.podcrash.api.mc.mob;

import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.World;

import java.lang.reflect.Field;

public class RandomArrow extends EntityArrow {
    public RandomArrow(World world) {
        super(world);
    }

    public RandomArrow(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public RandomArrow(World world, EntityLiving entityliving, EntityLiving entityliving1, float f, float f1) {
        super(world, entityliving, entityliving1, f, f1);
    }

    public RandomArrow(World world, EntityLiving entityliving, float f) {
        super(world, entityliving, f);
    }

    public void randomShoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 /= f2;
        d1 /= f2;
        d2 /= f2;
        d0 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * f1;
        d1 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * f1;
        d2 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * f1;
        d0 *= f;
        d1 *= f;
        d2 *= f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        this.lastYaw = this.yaw = (float) (FastMath.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        this.lastPitch = this.pitch = (float) (FastMath.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
        try {
            Field apField = this.getClass().getField("ap");
            apField.setAccessible(true);
            apField.set(this, 0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
