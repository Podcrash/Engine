package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.events.ItemCollideEvent;
import com.podcrash.api.mc.location.BoundingBox;
import com.podcrash.api.mc.location.RayTracer;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to look for an item and see if it either hits the ground or an entity
 */
public class ItemIntercept extends CallbackAction<ItemIntercept> {
    //The variable below is made to ensure the bukkit events only run at least ONCE per player.
    //it should NEVER run more than ONCE per player.
    private Set<Integer> avoids;

    protected LivingEntity entity;
    private Location interceptLocation;

    protected Item item;
    protected double radius;

    private ItemIntercept(long delay, long ticks) {
        super(delay, ticks);
    }
    public ItemIntercept(Item item, double radius) {
        this(0, 1);
        this.item = item;
        this.radius = radius;

        avoids = new HashSet<>();
    }

    @Override
    public boolean cancel() {
        World world = item.getWorld();
        if(!item.isValid() || EntityUtil.onGround(item)) return true;
        Vector dir = item.getVelocity();
        Location itemLocation = item.getLocation();
        for(LivingEntity living : world.getLivingEntities()){
            int entityID = living.getEntityId();
            if(avoids.contains(entityID)) continue;

            //the event is going to be called first before the super advanced projectile calculations
            //so that it will not perform them as much
            ItemCollideEvent event = new ItemCollideEvent(living, item);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                avoids.add(entityID);
                return false;
            }

            Vector itemVector = itemLocation.toVector();
            BoundingBox box = new BoundingBox(living);

            Block block = itemLocation.add(dir).getBlock();
            BoundingBox blockBox = new BoundingBox(block);
            //0.3 is the default for arrows.
            Vector intercept;
            if ((intercept = projectile2DHit(0.3, radius, item.getVelocity(), itemVector, box)) != null) {
                /** Deprecated, replaced by the above.
                 if(living instanceof Player) {
                 if(!GameManager.getGame().isParticipating((Player) living) || GameManager.getGame().isRespawning((Player) living))
                 return false;
                 }**/
                this.entity = living;
                this.interceptLocation = intercept.toLocation(living.getWorld());
                return true;
            } else if (!BlockUtil.isPassable(block)) {
                Vector v = projectile2DHit(0.1, 10, item.getVelocity(), itemVector, box);
                this.interceptLocation = v.toLocation(item.getWorld());
                this.entity = null;
                return true;
            }
        }
        return false;
    }

    /**
     * this check looks at the 2 vectors, the projectile's velocity and the vector of the projectile's position
     * and sees via ray tracing it will hit
     * TODO: make this a util method
     * @param expectedGrowth expand the hitbox by these directions (squared)
     * @param projVelo the velocity of the projectile
     * @param projLoc the location of the entity
     * @return if the projectile has hit.
     */
    private Vector projectile2DHit(double expectedGrowth, double distance, Vector projVelo, Vector projLoc, BoundingBox box) {
        //grow the box
        box = box.grow(expectedGrowth);

        RayTracer tracer = new RayTracer(projLoc, projVelo);
        //the accuracy by default is 0.8, there is no need to make it lower to have an extremely fine detection for hitboxes
        //that are basically 1 block wide
        Vector v = tracer.positionOfIntersection(box, distance, 0.8);
        return v;
    }
    @Override
    public void cleanup() {
        super.cleanup();
        this.avoids.clear();
    }

    public Location getInterceptLocation() {
        return interceptLocation;
    }
    public LivingEntity getIntercepted() {
        return entity;
    }
}
