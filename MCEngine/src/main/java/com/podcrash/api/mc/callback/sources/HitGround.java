package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.entity.Entity;

/**
 * This class is used to await until the entity in question touches the ground (and isn't moving)
 */
public class HitGround extends CallbackAction<HitGround> {

    public HitGround(Entity entity) {
        super(0, 1);
        //if the entity is on the ground AND it's not moving.
        this.changeEvaluation(() ->
                EntityUtil.onGround(entity) && entity.getVelocity().lengthSquared() < 0.0001);
    }
}
