package com.podcrash.api.mc.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.mc.callback.sources.AwaitTime;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.world.SpawnWorldSetter;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.db.redis.Communicator;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SpigotJoinListener extends ListenerBase {
    public SpigotJoinListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void initialSpawn(PlayerInitialSpawnEvent e) {
        Game game;
        //if the game is currently running, don't do anything with the spawn location
        if((game = GameManager.getGame()) != null) {
            if(game.getGameState() == GameState.STARTED) return;
        }
        SpawnWorldSetter worldSetter = Pluginizer.getSpigotPlugin().getWorldSetter();
        if(worldSetter.getCurrentWorldName() == null) return;
        World spawnWorld = Bukkit.getWorld(worldSetter.getCurrentWorldName());
        e.setSpawnLocation(spawnWorld.getSpawnLocation());
    }

    private void putPlayerDB(UUID uuid) {
        PlayerTable players = TableOrganizer.getTable(DataTableType.PLAYERS);
        players.insert(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(Communicator.isGameLobby())
            new HitDetectionInjector(player).injectHitDetection();
        ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1);
        player.setWalkSpeed(0.2F);
        PodcrashSpigot.getInstance().getLogger().info(((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue() + "");
        PodcrashSpigot.getInstance().getLogger().info("join SPIGOTJOIN");

        putPlayerDB(player.getUniqueId());
        PodcrashSpigot.getInstance().setupPermissions(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void joinMessage(PlayerJoinEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.DARK_GRAY).append('[')
                .append(ChatColor.GRAY).append("Join")
                .append(ChatColor.DARK_GRAY).append("] ")
                .append(ChatColor.YELLOW).append(event.getPlayer().getDisplayName());
        event.setJoinMessage(null);
        String joinMessage = builder.toString();
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(joinMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void leave(PlayerQuitEvent event) {
        HitDetectionInjector.getHitDetection(event.getPlayer()).deinject();
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.DARK_GRAY).append('[')
                .append(ChatColor.GRAY).append("Leave")
                .append(ChatColor.DARK_GRAY).append("] ")
                .append(ChatColor.YELLOW).append(event.getPlayer().getDisplayName());
        event.setQuitMessage(null);
        String quitMessage = builder.toString();
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(quitMessage);
        }

        if(GameManager.getGame() == null) return;
        Game game = GameManager.getGame();
        GameState state = game.getGameState();
        switch (state) {
            case STARTED:
                if(Bukkit.getOnlinePlayers().size() == 1)
                    GameManager.endGame(game);
                break;
            case LOBBY:
                GameManager.removePlayer(event.getPlayer());
                break;
            default:
                break;
        }

    }
}
