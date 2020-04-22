package com.podcrash.api.mc.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import com.podcrash.api.mc.game.GTeam;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

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


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void join(PlayerJoinEvent event) {
        PodcrashSpigot spigot = PodcrashSpigot.getInstance();
        Logger log = spigot.getLogger();
        Player player = event.getPlayer();
        log.info("34ewrf");
        if(Communicator.isGameLobby()) {
            new HitDetectionInjector(player).injectHitDetection();
        }
        lobbyGameEnter(player);
        log.info("test123");

        resetAttributes(player);

        putPlayerDB(player.getUniqueId());

        PodcrashSpigot.getInstance().setupPermissions(player);

        setUpHostPermissions(player);
    }

    private void putPlayerDB(UUID uuid) {
        PlayerTable players = TableOrganizer.getTable(DataTableType.PLAYERS);
        players.insert(uuid);
    }

    private void lobbyGameEnter(Player player) {
        if(GameManager.getGame() != null && !GameManager.getGame().getGameState().equals(GameState.LOBBY)) return;
        DamageApplier.addInvincibleEntity(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        //ItemStackUtil.createItem(player.getInventory(), 388, 1, 2, "&a&lEnable Lobby PVP");
    }
    private void resetAttributes(Player player) {
        //having trouble finding out if this method is actually useful
        ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1);
        player.setWalkSpeed(0.2F);
    }

    private void setUpHostPermissions(Player possibleHost) {
        PodcrashSpigot spigot = PodcrashSpigot.getInstance();
        UUID uuid = possibleHost.getUniqueId();
        if (!uuid.equals(spigot.getPPLOwner())) return;

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        table.getRankAsync("HOST").thenAccept(rank -> {
            Set<String> permissions = rank.getPermissions();
            PermissionAttachment attachment = spigot.getPlayerPermissions().get(uuid);
            for(String perm : permissions) {
                attachment.setPermission(perm, true);
            }
            possibleHost.sendMessage("You are the host of this server!");
        });
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
        HitDetectionInjector injector = HitDetectionInjector.getHitDetection(event.getPlayer());
        if(injector != null) injector.deinject();
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
                if(game.isSpectating(event.getPlayer()))
                    game.removePlayer(event.getPlayer());
                else {
                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    List<GTeam> teams = game.getTeams();
                    if(teams == null) return;
                    GTeam playerTeam = game.getTeam(event.getPlayer());
                    if(playerTeam == null) return;
                    int onlineSize = 0;
                    for(UUID uuid : playerTeam.getPlayers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if(player == null) continue;
                        if(player.isOnline())
                            onlineSize++;
                    }
                    System.out.println(onlineSize + " <-- size");
                    /*
                    if(onlineSize <= 1) {
                        GameManager.endGame(game);
                        Game newGame;
                        if((newGame = GameManager.getGame()) == null) return;
                        newGame.remove(event.getPlayer());
                    }

                     */
                }
                break;
            case LOBBY:
                GameManager.removePlayer(event.getPlayer());
                break;
            default:
                break;
        }

    }
}
