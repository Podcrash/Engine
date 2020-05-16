package com.podcrash.api.callback.helpers;

import com.podcrash.api.callback.sources.AwaitTime;
import com.podcrash.api.callback.sources.HitGround;
import com.podcrash.api.events.TrapPrimeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.util.HashSet;
import java.util.Set;

/**
 * Use this class to spawn items, await until they touch the ground, and prime them!
 */
public final class TrapSetter {
    private final static Set<Integer> trapIds = new HashSet<>();

    /**
     *
     * @param item the item being used to make the trap
     * @param elapsed how long the trap should remain until it is primed (in milliseconds)
     */
    public static void spawnTrap(Item item, long elapsed) {
        HitGround ground = new HitGround(item);
        AwaitTime time = new AwaitTime(elapsed);
        trapIds.add(item.getEntityId());
        item.setPickupDelay(100000);
        time.then(() -> {
            if (!trapIds.contains(item.getEntityId())) return;
            item.setPickupDelay(0);
            TrapPrimeEvent primeEvent = new TrapPrimeEvent(item);
            Bukkit.getPluginManager().callEvent(primeEvent);
        });

        ground.then(() -> time.runAsync(1, 0)).runAsync(1, 0);
    }


    /**
     * For whatever reason, the method below is not being used to test whether or not the item is a trap.
     * @param item - the item that's being tested
     * @return if the item is a trap
     */
    public static boolean isTrap(Item item) {
        return trapIds.contains(item.getEntityId());
    }

    /**
     * The method is supposed to test if the item is actually a trap spawned from TrapSetter#spawnTrap
     * @param item the supposed trap of the item
     * @return whether or not the remove was successful (doubles as a contains)
     */
    public static boolean deleteTrap(Item item) {
        if (item == null)
            return false;
        return trapIds.remove(item.getEntityId());
    }

    public static boolean destroyTrap(Item item) {
        boolean delete = deleteTrap(item);
        item.remove();
        return delete;
    }
}
