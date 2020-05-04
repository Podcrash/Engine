package com.podcrash.api.callback;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

public interface InterceptCallback extends ICallback {
    void dorun(Item item, LivingEntity entity, Location interceptLocation);
}
