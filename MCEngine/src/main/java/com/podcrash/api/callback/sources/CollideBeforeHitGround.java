package com.podcrash.api.callback.sources;

import com.podcrash.api.callback.CallbackAction;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This is used when a player uses a skill or similar that requires being in the air and having dropped back down to the ground
 */
public class CollideBeforeHitGround extends CallbackAction<CollideBeforeHitGround> {
    private final Entity entity;
    private final double x;
    private final double y;
    private final double z;

    public CollideBeforeHitGround(Entity entity, long delay, double hitboxX, double hitboxY, double hitBoxZ) {
        super(delay, 1);
        this.entity = entity;
        this.x = hitboxX;
        this.y = hitboxY;
        this.z = hitBoxZ;
        changeEvaluation(() -> (
                getValidEntitiesInRange(this.entity,x,y,z).size() > 0) ||
                EntityUtil.onGround(this.entity));
    }
    public CollideBeforeHitGround(Player entity) {
        this(entity, 1, 1.15, 1.15, 1.15);
    }
    public CollideBeforeHitGround(Player entity, long delay) {
        this(entity, delay, 1.15, 1.15, 1.15);
    }

    public static List<Entity> getValidEntitiesInRange(Entity entity, double x, double y, double z) {
        List<Entity> entities = entity.getNearbyEntities(x,y,z);
        Game game = GameManager.getGame();
        for (int i = 0; i < entities.size(); i++) {
            Entity anEntity = entities.get(i);
            if (!(anEntity instanceof LivingEntity)
                    || (anEntity instanceof Player &&
                    (!game.isParticipating((Player) anEntity) || game.isRespawning((Player) anEntity)))) {
                entities.remove(i);
                i--;
            }
        }
        return entities;
    }


}
