package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * This class is used to await until the entity in question touches the ground (and isn't moving)
 */
public class HitGround extends CallbackAction<HitGround> {
    private Entity entity;
    private Vector previousVector;

    public HitGround(Entity entity) {
        super(0, 1);
        //if the entity is on the ground AND it's not moving.
        this.previousVector = entity.getLocation().toVector();
        this.entity = entity;
        this.changeEvaluation(() ->
                (EntityUtil.onGround(entity) || isInABlock()) && !isMoving());
    }

    /**
     *
     * @return true if is moving, false if is (effecitvely) not moving
     */
    private boolean isMoving() {
        Vector currentVector = this.entity.getLocation().toVector();
        return currentVector.distanceSquared(previousVector) > 0.05;
    }

    private boolean isInABlock() {
        Block block = previousVector.toLocation(entity.getWorld()).getBlock();
        Material type = block.getType();
        return type.isSolid() || !type.isSolid();
    }
}
