package com.podcrash.api.mc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public class MobListeners implements Listener {
  
	@EventHandler(priority = EventPriority.LOW)
	 public void onEntityCombust(EntityCombustEvent event){
		 if(event.getEntity() instanceof Monster){
			 event.setCancelled(true);
		 }
	 }
   
   @EventHandler(priority = EventPriority.LOW)
    public void onMobDamage(EntityDamageEvent e) {

        Iterator<?> mobsIterator = mobs.entrySet().iterator();
        while (mobsIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)mobsIterator.next();
            MobData mob = mobs.get(mapElement.getKey());
            if (!mob.getDamageable()) {
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
