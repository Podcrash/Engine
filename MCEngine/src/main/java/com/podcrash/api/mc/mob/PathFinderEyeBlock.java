package com.podcrash.api.mc.mob;

import com.podcrash.api.mc.world.BlockUtil;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * This is used for illusion
 */
public class PathFinderEyeBlock extends PathfinderGoal {
    private final Player player;
    private final EntityCreature entitycreature;

    public PathFinderEyeBlock(Player player, EntityCreature entitycreature) {
        this.player = player;
        this.entitycreature = entitycreature;

    }

    @Override
    public boolean a() {
        return player != null;
    }

    @Override
    public boolean b() {
        return false;
    }

    @Override
    public void c() {
        //Vec3D vec3d = RandomPositionGenerator.a(this.entitycreature, 5, 4);
        //if (vec3d == null) return; // IN AIR

        Location eyeLocation = BlockUtil.getHighestUnderneath(this.entitycreature.getBukkitEntity().getLocation().add(player.getLocation().getDirection().normalize().multiply(2)));

        PathEntity pathEntity = this.entitycreature.getNavigation().a(eyeLocation.getBlockX(), eyeLocation.getBlockY(), eyeLocation.getBlockZ());
        this.entitycreature.getNavigation().a(pathEntity, 2);

    }

    @Override
    public void e() {

        //this is massive
        Location loc = player.getTargetBlock((HashSet<Byte>) null, 100).getLocation();
        if(loc == null){
            loc = BlockUtil.getHighestUnderneath(this.entitycreature.getBukkitEntity().getLocation().add(player.getLocation().getDirection().normalize().setY(this.entitycreature.getBukkitEntity().getLocation().getY()).multiply(2)));
        }
        PathEntity pathEntity = this.entitycreature.getNavigation().a(loc.getX(), loc.getY(), loc.getZ());

        this.entitycreature.getNavigation().a(pathEntity, 2);

    }


}
