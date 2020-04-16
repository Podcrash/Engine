package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.plugin.PodcrashSpigot;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StatusListener extends ListenerBase {
    public StatusListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void damage(DamageApplyEvent event) {
        if(!(event.getVictim() instanceof Player)) return;
        StatusApplier applier = StatusApplier.getOrNew(event.getVictim());
        if(applier.has(Status.ROOTED))
            event.setDoKnockback(false);
    }

    /**
     * This might not work for fortitude
     * @param e
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void heal(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        //if(e.getRegainReason())
        if(StatusApplier.getOrNew(player).has(Status.BLEED)) e.setCancelled(true);
    }
}
