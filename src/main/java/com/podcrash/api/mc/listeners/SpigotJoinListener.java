package com.podcrash.api.mc.listeners;

import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.mc.callback.sources.AwaitTime;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.world.SpawnWorldSetter;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.db.redis.Communicator;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotJoinListener extends ListenerBase {
    public SpigotJoinListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void initialSpawn(PlayerInitialSpawnEvent e) {
        Game game;
        //if the game is currently running, don't do anything with the spawn location
        if((game = GameManager.getGame()) != null) {
            if(game.isOngoing()) return;
        }
        SpawnWorldSetter worldSetter = Pluginizer.getSpigotPlugin().getWorldSetter();
        if(worldSetter.getCurrentWorldName() == null) return;
        World spawnWorld = Bukkit.getWorld(worldSetter.getCurrentWorldName());
        e.setSpawnLocation(spawnWorld.getSpawnLocation());
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
