package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.entity.Player;

/**
 * This is used when a player uses a skill or similar that requires being in the air and having dropped back down to the ground
 */
public class CollideBeforeHitGround extends CallbackAction<CollideBeforeHitGround> {
    private Player player;

    public CollideBeforeHitGround(Player player, long delay) {
        super(delay, 1);
        this.player = player;
        this.changeEvaluation(() -> (
                player.getNearbyEntities(1.15, 1.15, 1.15).size() > 0) ||
                EntityUtil.onGround(player));
    }
    public CollideBeforeHitGround(Player player) {
        this(player, 1);
    }
}
