package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.mob.MobData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

import static com.podcrash.api.mc.mob.MobManager.mobs;

public class MobListeners extends ListenerBase {
    public MobListeners(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent e) {
        Collection<LivingEntity> entities = e.getAffectedEntities();

        for (LivingEntity entity: entities) {

            int id = entity.getEntityId();
            MobData mob = mobs.get(id);

            if (mob == null)
                continue;

            if (!mob.takesPotionEffects()) {
                Collection<PotionEffect> entityEffects = entity.getActivePotionEffects();
                for (PotionEffect entityEffect : entityEffects) {
                    entity.removePotionEffect(entityEffect.getType());
                }
            }


        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityCombust(EntityCombustEvent e){
        int id = e.getEntity().getEntityId();
        MobData mob = mobs.get(id);

        if (mob == null)
            return;

        if (!mob.canBurn())
            e.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMobDamage(EntityDamageEvent e) {
        if (e.isCancelled())
            return;
        int id = e.getEntity().getEntityId();
        MobData mob = mobs.get(id);

        if (mob == null)
            return;

        if (!mob.isDamageable())
            e.setCancelled(true);

    }
}
