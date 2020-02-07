package com.podcrash.api.mc.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.Collection;

import com.podcrash.api.mc.mob.MobManager.mobs;
import com.podcrash.api.mc.mob.MobData;

public class MobListeners extends ListenerBase {
	@EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent e) {
        Collection<LivingEntity> entities = e.getAffectedEntities();
        ThrownPotion effect = e.getPotion();

        for (LivingEntity entity: entities) {

            int id = entity.getEntityId();
            MobData mob = mobs.get(id);

            if (mob == null) {
                continue;
            } else {
                if (!mob.takesPotionEffects()) {
                    Collection<PotionEffect> entityEffects = entity.getActivePotionEffects();
                    for (PotionEffect entityEffect : entityEffects) {
                        entity.removePotionEffect(entityEffect.getType());
                    }
                }
            }

        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityCombust(EntityCombustEvent e){
        int id = e.getEntity().getEntityId();
        MobData mob = mobs.get(id);

        if (mob == null) {
            return;
        } else {
            if (!mob.canBurn()) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMobDamage(EntityDamageEvent e) {

        int id = e.getEntity().getEntityId();
        MobData mob = mobs.get(id);

        if (mob == null) {
            return;
        } else {
            if(!mob.isDamageable()) {
                e.setCancelled(true);
            } else {
                return;
            }
        }
}
