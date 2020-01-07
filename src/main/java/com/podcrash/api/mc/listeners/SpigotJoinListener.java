package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.redis.Communicator;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotJoinListener extends ListenerBase {
    public SpigotJoinListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        if(Communicator.isGameLobby())
            new HitDetectionInjector(event.getPlayer()).injectHitDetection();
        ((CraftPlayer) event.getPlayer()).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1);
        event.getPlayer().setWalkSpeed(0.2F);
        PodcrashSpigot.getInstance().getLogger().info(((CraftPlayer) event.getPlayer()).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue() + "");
        PodcrashSpigot.getInstance().getLogger().info("join SPIGOTJOIN");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void leave(PlayerQuitEvent event) {
        HitDetectionInjector.getHitDetection(event.getPlayer()).deinject();
    }
}
