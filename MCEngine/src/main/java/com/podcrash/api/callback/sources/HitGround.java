package com.podcrash.api.callback.sources;

import com.podcrash.api.callback.CallbackAction;
import com.podcrash.api.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * This class is used to await until the entity in question touches the ground (and isn't moving)
 */
public class HitGround extends CallbackAction<HitGround> {
    private final Entity entity;
    private final Vector previousVector;

    public HitGround(Entity entity) {
        super(0, 1);
        //if the entity is on the ground AND it's not moving.
        this.previousVector = entity.getLocation().toVector();
        this.entity = entity;
        this.changeEvaluation(() ->
                (EntityUtil.onGround(entity) || isInABlock()) && !isMoving());
    }

    /**
     * Finds if the entity is moving or not
     * @return true if is moving, false if is (effectively) not moving
     */
    private boolean isMoving() {
        Vector currentVector = this.entity.getLocation().toVector();
        return currentVector.distanceSquared(previousVector) > 0.05;
    }

    /**
     * Finds if the entity is in a block
     * @return if the entity is in a block
     */
    private boolean isInABlock() {
        Block block = previousVector.toLocation(entity.getWorld()).getBlock();
        Material type = block.getType();
        return type.isSolid() || !type.isSolid();
    }
}
