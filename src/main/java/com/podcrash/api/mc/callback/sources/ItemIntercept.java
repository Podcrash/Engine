package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.callback.CallbackAction;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * This class is used to look for an item and see if it either hits the ground or an entity
 */
public class ItemIntercept extends CallbackAction<ItemIntercept> {
    protected LivingEntity entity;
    protected Player owner;
    protected Item item;
    private ItemIntercept(long delay, long ticks) {
        super(delay, ticks);
    }

    public ItemIntercept(Player owner, Item item) {
        this(0, 1);
        this.owner = owner;
        this.item = item;
    }

    @Override
    public boolean cancel() {
        World world = item.getWorld();
        if(!item.isValid() || EntityUtil.onGround(item)) return true;
        for(LivingEntity living : world.getLivingEntities()){
            if(living.getEntityId() != owner.getEntityId()){
                Location location = living.getLocation();
                Vector dir = item.getLocation().getDirection().clone().normalize();
                Location itemClone = item.getLocation().clone();
                Location locationClone = location.clone();
                itemClone.setY(0);
                locationClone.setY(0);
                if((location.getY() <= item.getLocation().getY() && item.getLocation().getY() < location.getY() + 2) && itemClone.distanceSquared(locationClone) <= 1.1) {
                    if(living instanceof Player) {
                        if(!GameManager.getGame().isParticipating((Player) living) || GameManager.getGame().isRespawning((Player) living))
                            return false;
                    }
                    this.entity = living;
                    return true;
                }else if(!BlockUtil.isPassable(item.getLocation().add(dir.multiply(1.5D)).getBlock())){
                    this.entity = null;
                    return true;
                }
            }
        }
        return false;
    }

    public LivingEntity getIntercepted() {
        return entity;
    }
}
