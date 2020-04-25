package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.events.game.GameEndEvent;
import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.game.resources.GameResource;
import com.podcrash.api.mc.game.resources.HealthBarResource;

import com.podcrash.api.plugin.Pluginizer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Waits for spots to open in a game, prompts spectators to fill.
 * @author poetahto
 */
public class BackfillListener extends ListenerBase {
    private static boolean canBackfill = false;                         // Whether or not the game has an open spot for a player to join.
    private static Map<UUID, Pair<Player, GTeam>> offlinePlayers = new HashMap<>(); // A list of all players that were participating, but are currently offline.

    public BackfillListener(JavaPlugin plugin) {
        super(plugin);
    }

    public static boolean backfillSpectatorIntoGame(Player joiningPlayer, TeamEnum teamEnum) {
        Game game = GameManager.getGame();
        GTeam team = game.getTeam(teamEnum);
        if (game.getGameState().equals(GameState.STARTED) && team.getPlayers().size() < team.getMaxPlayers() && GameManager.isSpectating(joiningPlayer)) {
            joiningPlayer.setGameMode(GameMode.SURVIVAL);
            game.addParticipant(joiningPlayer);

            //TODO below is literally just the GameManager.randomTeam code, but uses game.joinTeam instead of gameManager.joinTeam - make it better idk
            int red = game.getTeam(0).teamSize();
            int blue = game.getTeam(1).teamSize();
            if(blue > red)
                game.joinTeam(joiningPlayer, TeamEnum.RED, true);
            else if(red > blue)
                game.joinTeam(joiningPlayer, TeamEnum.BLUE, true);
            else //they are equal, good-ol RNG!
                game.joinTeam(joiningPlayer, new TeamEnum[]{TeamEnum.RED, TeamEnum.BLUE}[(int) (Math.random() + 0.5D)], true);

            joiningPlayer.teleport(game.getTeam(joiningPlayer).getSpawn(joiningPlayer));
            for (GameResource resource : game.getGameResources()) {
                if (resource instanceof HealthBarResource)
                    ((HealthBarResource) resource).addPlayerToMap(joiningPlayer);
            }

            if (!game.isFull()) {
                for (Player p : GameManager.getGame().getBukkitSpectators()) {
                    sendCanJoinMessage(p);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Attempt to replace an absent player with a spectator.
     * @param joiningPlayer The player that wants to join the game.
     * @param absentPlayerIGN The player that is to be replaced.
     * @return Whether the player successfully joined
     */
    public static boolean replaceOfflineWithSpectator(Player joiningPlayer, String absentPlayerIGN) {
        Player absentPlayer = getLastOnlineFromString(absentPlayerIGN);
        if(canBackfill && absentPlayer != null && GameManager.isSpectating(joiningPlayer)) {
            Game game = GameManager.getGame();
            joiningPlayer.setGameMode(GameMode.SURVIVAL);

            game.removePlayer(absentPlayer);
            game.addParticipant(joiningPlayer);
            game.joinTeam(joiningPlayer, offlinePlayers.get(absentPlayer.getUniqueId()).getValue().getTeamEnum(), true);

            offlinePlayers.remove(absentPlayer.getUniqueId());
            updateCanBackfill();

            joiningPlayer.teleport(GameManager.getGame().getTeam(joiningPlayer).getSpawn(joiningPlayer));
            for (GameResource resource : game.getGameResources()) {
                if (resource instanceof HealthBarResource)
                    ((HealthBarResource) resource).addPlayerToMap(joiningPlayer);
            }

            if (canBackfill) {
                for (Player p : GameManager.getGame().getBukkitSpectators()) {
                    List<UUID> keysAsArray = new ArrayList<>(offlinePlayers.keySet());
                    UUID id = keysAsArray.get(0);
                    sendCanReplaceMessage(p, id, offlinePlayers.get(id).getKey().getName());
                }
            }

            return true;
        }
        return false;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Game game = GameManager.getGame();
        Player absentPlayer = event.getPlayer();
        // If the player that left was actually participating and the game had started, add them to the offline players list.
        if (game.isParticipating(absentPlayer) && game.getGameState().equals(GameState.STARTED)) {
            offlinePlayers.put(absentPlayer.getUniqueId(), new ImmutablePair<>(absentPlayer,game.getTeam(absentPlayer)));
            updateCanBackfill();
            // Tell everybody that is spectating that a spot has opened up for them.
            for (Player joiningPlayer : game.getBukkitSpectators()){
                sendCanReplaceMessage(joiningPlayer, absentPlayer.getUniqueId(), game.getTeam(absentPlayer).getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Game game = GameManager.getGame();
        offlinePlayers.remove(event.getPlayer().getUniqueId());
        updateCanBackfill();
        if (!GameManager.getGame().isParticipating(event.getPlayer())) {
            if (canBackfill) {
                List<UUID> keysAsArray = new ArrayList<>(offlinePlayers.keySet());
                UUID id = keysAsArray.get(0);
                sendCanReplaceMessage(event.getPlayer(), id, offlinePlayers.get(id).getKey().getName());
            } else if (!game.isFull()) {
                sendCanJoinMessage(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        offlinePlayers.clear();
        canBackfill = false;
    }

    /**
     * Update canBackfill to reflect whether the offlinePlayers set is empty or not.
     */
    private static void updateCanBackfill() {
        canBackfill = !offlinePlayers.isEmpty();
    }

    /**
     * Search the offlinePlayers list and return the player with the requested name.
     * @param name The player's IGN that you are searching for.
     * @return The last known player instance kept in Game that corresponds with the IGN supplied: can be null
     */
    private static Player getLastOnlineFromString(String name) {
        //Find the player in current game
        for (UUID id : offlinePlayers.keySet()) {
            Player player = offlinePlayers.get(id).getKey();
            if (name.equalsIgnoreCase(player.getName())) {
                return player;
            }
        }
        return null;
    }

    private static void sendCanReplaceMessage(Player joiningPlayer, UUID absentPlayerID, String teamName) {
        OfflinePlayer absentPlayer = Bukkit.getOfflinePlayer(absentPlayerID);
        Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(),
                () -> joiningPlayer.sendMessage(String.format("%s%sA spot has opened up for you on the %s team. \nType \"/accept %s\" to join!",
                        ChatColor.LIGHT_PURPLE,
                        ChatColor.BOLD,
                        teamName,
                        absentPlayer.getName())), 5 );
    }

    private static void sendCanJoinMessage(Player joiningPlayer) {
        Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(),
                () -> joiningPlayer.sendMessage(String.format("%s%sA spot is open for you in this game! \nType \"/accept\" to join!",
                ChatColor.LIGHT_PURPLE,
                ChatColor.BOLD)), 5 );
    }
}
