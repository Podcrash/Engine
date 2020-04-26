package com.podcrash.api.mc.effect.status;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * THis is supposed to be used to apply arrows to status effects
 */
public class ThrowableStatusApplier {
    private static final Multimap<Arrow, StatusWrapper> arrowStatuses = ArrayListMultimap.create();

    private ThrowableStatusApplier() {

    }

    public static void applyProj(StatusWrapper status, Arrow arrow) {
        arrowStatuses.put(arrow, status);
    }
    public static void applyProj(StatusWrapper status, Arrow... arrows) {
        for (Arrow arrow : arrows) {
            applyProj(status, arrow);
        }
    }

    public static void apply(Arrow arrow, Entity entity) {
        if (!arrowStatuses.containsKey(arrow)) {
            return;
        }
        Collection<StatusWrapper> statuses = arrowStatuses.get(arrow);
        StatusApplier applier = StatusApplier.getOrNew((Player) entity);
        for (StatusWrapper status : statuses) {
            applier.applyStatus(status);
        }
    }
}
