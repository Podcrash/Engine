package com.podcrash.api.mc.callback.sources;

import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * //TODO: FIX ALL OF THIS, callbacks are very confusing and hard to understand
 * This class is used to look for an item and see if it either hits the ground or an entity, or a certain time has passed
 */
public class DelayItemIntercept extends ItemIntercept {
    private final long duration;
    public DelayItemIntercept(Item item, float duration) {
        super(item, 1.5);
        this.duration = System.currentTimeMillis() + (1000L * (long) duration);
    }

    @Override
    public boolean cancel() {
        if(super.cancel()) return true;
        return System.currentTimeMillis() > this.duration;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        item.remove();
    }
}
