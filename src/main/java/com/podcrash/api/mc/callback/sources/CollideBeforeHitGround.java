package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * This is used when a player uses a skill or similar that requires being in the air and having dropped back down to the ground
 */
public class CollideBeforeHitGround extends CallbackAction<CollideBeforeHitGround> {
    private Entity entity;

    public CollideBeforeHitGround(Entity entity, long delay) {
        super(delay, 1);
        this.entity = entity;
        this.changeEvaluation(() -> (
                this.entity.getNearbyEntities(1.15, 1.15, 1.15).size() > 0) ||
                EntityUtil.onGround(this.entity));
    }
    public CollideBeforeHitGround(Player entity) {
        this(entity, 1);
    }
}
