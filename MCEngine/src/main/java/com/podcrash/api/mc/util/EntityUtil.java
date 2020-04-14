package com.podcrash.api.mc.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public final class EntityUtil  {
    /**
     * Uses the default onground check first.
     * Then it goes to our custom check.
     * @param entity
     * @return whether or not the entity is close to the ground
     */
    public static boolean onGround(Entity entity) {
        return onGround(entity, 0.1);
    }

    public static boolean onGround(Entity entity, double acc) {
        if(entity.isOnGround()) return true;
        Location location = entity.getLocation();
        return location.subtract(new Vector(0, acc, 0)).getBlock().getType().isSolid();
    }

    /**
     * Check to see if a player is lower than OR equal to a certain amount of health
     * @param entity
     * @param health
     * @return
     */
    public static boolean isBelow(LivingEntity entity, double health) {
        return entity.getMaxHealth() * health >= entity.getHealth();
    }
}
