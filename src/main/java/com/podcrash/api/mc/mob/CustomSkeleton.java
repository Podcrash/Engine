package com.podcrash.api.mc.mob;

import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;

public class CustomSkeleton extends EntitySkeleton {
    private Player player;

    public CustomSkeleton(net.minecraft.server.v1_8_R3.World world) {
        super(world);
        this.player = null;
    }

    public CustomSkeleton(World world, Player player) {
        super(((CraftWorld) world).getHandle());
        this.player = player;

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());


        } catch (Exception e) {
            e.printStackTrace();
        }

        this.goalSelector.a(10, new PathFinderEyeBlock(player, this));
/*

        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));

        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, false));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 0F));
        */
    }

    @Override
    protected net.minecraft.server.v1_8_R3.Item getLoot() {
        if (this.player == null) return super.getLoot();
        else return null;
    }

    @Override
    protected void dropDeathLoot(boolean flag, int i){
        if (this.player == null) super.dropDeathLoot(flag, i);
    }

    @Override
    protected void getRareDrop() {
        if(this.player == null) super.getRareDrop();
    }

    @Override
    protected void a(EntityItem entityItem){
        if(this.player == null) super.a(entityItem);
    }

    @Override
    protected String z() {
        if (this.player == null) return super.z();
        else return null;
    }

    @Override
    public void makeSound(String s, float f, float f1) {
        if(this.player == null) super.makeSound(s, f, f1);
    }

    @Override
    protected String bp() {
        if (this.player == null) return super.bp();
        else return null;
    }

    @Override
    protected String bo() {
        if (this.player == null) return super.bo();
        else return null;
    }

    public void addWorld(World world) {
        ((CraftWorld) world).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
