package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DelayItemIntercept extends ItemIntercept {
    private final long duration;
    public DelayItemIntercept(Player owner, Item item, float duration) {
        super(owner, item);
        this.duration = System.currentTimeMillis() + (1000L * (long) duration);
    }

    @Override
    public boolean cancel() {
        World world = item.getWorld();
        if(!item.isValid()) return true;
        if(!EntityUtil.onGround(item)) {
            for(LivingEntity living : world.getLivingEntities()){
                if(living != owner){
                    Location location = living.getLocation();
                    Vector dir = item.getLocation().getDirection().clone().normalize();
                    Location itemClone = item.getLocation().clone();
                    Location locationClone = location.clone();
                    itemClone.setY(0);
                    locationClone.setY(0);
                    if((location.getY() <= item.getLocation().getY() && item.getLocation().getY() < location.getY() + 2) && itemClone.distanceSquared(locationClone) <= 1.1) {
                        this.entity = living;
                        return true;
                    }else if(!BlockUtil.isPassable(item.getLocation().add(dir.multiply(1.5D)).getBlock())){
                        this.entity = null;
                        return true;
                    }
                }
            }
        }
        return System.currentTimeMillis() > this.duration;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        item.remove();
    }
}
