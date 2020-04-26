package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class FallDamageHandler extends ListenerBase {
    private static final Set<String> guaranteeNullFall = new HashSet<>();

    public FallDamageHandler(JavaPlugin plugin) {
        super(plugin);
    }

    public static void guaranteeSafeFall(LivingEntity entity) {
        guaranteeNullFall.add(entity.getName());

        TimeHandler.repeatedTimeAsync(1, 15, new TimeResource() {
            @Override
            public void task() {

            }

            @Override
            public boolean cancel() {
                return EntityUtil.onGround(entity);
            }

            @Override
            public void cleanup() {
                guaranteeNullFall.remove(entity.getName());
            }
        });
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void fall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        event.setDamage(event.getDamage() - 3);//mini breakfall.

        String entry = event.getEntity().getName();
        if (guaranteeNullFall.contains(entry)) {
            event.setCancelled(true);
            event.setDamage(0);
            guaranteeNullFall.remove(entry);
        }
    }
}
