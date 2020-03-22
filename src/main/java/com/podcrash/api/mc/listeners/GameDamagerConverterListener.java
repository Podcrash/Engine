package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.disguise.Disguise;
import com.podcrash.api.mc.disguise.Disguiser;
import com.podcrash.api.mc.effect.status.ThrowableStatusApplier;
import com.podcrash.api.mc.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GameDamagerConverterListener extends ListenerBase {
    private static Map<Integer, Float> arrowDamageMap = new HashMap<>();
    private Map<String, Long> delay = new HashMap<>();

    public GameDamagerConverterListener(JavaPlugin plugin) {
        super(plugin);
    }

    public static void forceAddArrow(Arrow arrow, float charge) {
        arrowDamageMap.putIfAbsent(arrow.getEntityId(), charge);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void damage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getDamager();
        if (arrow == null || !arrowDamageMap.containsKey(arrow.getEntityId())) return;
        if(System.currentTimeMillis() < delay.getOrDefault(event.getEntity().getName(), 0L))
            return;
        Disguise possDisguise = Disguiser.getSeenDisguises().get(event.getEntity().getEntityId());
        if(handleDisguise(possDisguise, event)) return;
        event.setCancelled(true);
        double damage = 8 * arrowDamageMap.get(arrow.getEntityId());
        DamageApplier.damage((LivingEntity) event.getEntity(), (Player) ((Projectile) event.getDamager()).getShooter(), damage, arrow, true);
        delay.put(event.getEntity().getName(), System.currentTimeMillis() + 100);
        ThrowableStatusApplier.apply(arrow, event.getEntity());
        arrow.remove();
        arrowDamageMap.remove(arrow.getEntityId());
    }

    /**
     * Pass arrows to the original user
     */
    private boolean handleDisguise(Disguise possDisguise, EntityDamageByEntityEvent event) {
        if(possDisguise == null) return false;
        if(!(possDisguise.getEntity() instanceof Player)) {
            ((LivingEntity) possDisguise.getEntity()).damage(event.getDamage(), event.getDamager());
            return true;
        }
        Arrow arrow = (Arrow) event.getDamager();
        event.setCancelled(true);
        DamageApplier.damage((LivingEntity) event.getEntity(), (LivingEntity) arrow.getShooter(), event.getDamage(), arrow, true);
        ThrowableStatusApplier.apply(arrow, event.getEntity());
        arrow.remove();
        return true;
    }

    /**
     * Cancel shooting in water = not allowed
     * @param event
     */
    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void shootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            if(event.getEntity().getLocation().getBlock().getType() == Material.WATER ||
                    event.getEntity().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                event.setCancelled(true);
                return;
            }
            float force = event.getForce();
            Arrow arrow = (Arrow) event.getProjectile();
            arrowDamageMap.put(arrow.getEntityId(), force);
        }
    }

    /**
     * Remove arrows quickly
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void arrowLand(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }
}
