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

public class MobListeners extends ListenerBase {
  
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityCombust(EntityCombustEvent event){
		if(event.getEntity() instanceof Monster){
			event.setCancelled(true);
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
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent e) {
        Collection<LivingEntity> entities = e.getAffectedEntities();
        for (LivingEntity entity: entities) {
            if(entity instanceof Player) {
                return;
            } else {
                e.setCancelled(true);
            }
        }

    }
   
   
}
