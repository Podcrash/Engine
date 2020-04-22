package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.events.game.GameEndEvent;
import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.game.resources.GameResource;
import com.podcrash.api.mc.game.resources.HealthBarResource;
import com.podcrash.api.mc.time.TimeHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Waits for spots to open in a game, prompts spectators to fill.
 * @author poetahto
 */
public class BackfillListener extends ListenerBase {
    private static boolean canBackfill = false;                         // Whether or not the game has an open spot for a player to join.
    private static Map<Player, GTeam> offlinePlayers = new HashMap<>(); // A list of all players that were participating, but are currently offline.

    public BackfillListener(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Attempt to replace an absent player with a spectator.
     * @param joiningPlayer The player that wants to join the game.
     * @param absentPlayerIGN The player that is to be replaced.
     */
    public static boolean replaceOfflineWithSpectator(Player joiningPlayer, String absentPlayerIGN) {
        Player absentPlayer = getOfflineFromString(absentPlayerIGN);
        if(canBackfill && absentPlayer != null && GameManager.isSpectating(joiningPlayer)) {
            Game game = GameManager.getGame();
            joiningPlayer.setGameMode(GameMode.SURVIVAL);

            // Fixes the double damage bug
            joiningPlayer.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
            TimeHandler.delayTime(1L, () -> joiningPlayer.setItemInHand(new ItemStack(Material.AIR)));

            game.removePlayer(absentPlayer);
            game.addParticipant(joiningPlayer);
            game.joinTeam(joiningPlayer, offlinePlayers.get(absentPlayer).getTeamEnum(), true);

            joiningPlayer.teleport(GameManager.getGame().getTeam(joiningPlayer).getSpawn(joiningPlayer));
            for (GameResource resource : game.getGameResources()) {
                if(resource instanceof HealthBarResource) ((HealthBarResource) resource).addPlayerToMap(joiningPlayer);
            }

            offlinePlayers.remove(absentPlayer);
            updateCanBackfill();

            if (canBackfill) {
                for (Player p : GameManager.getGame().getBukkitSpectators()) {
                    List<Player> keysAsArray = new ArrayList<>(offlinePlayers.keySet());
                    sendCanJoinMessage(p, keysAsArray.get(0));
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
            offlinePlayers.put(absentPlayer, game.getTeam(absentPlayer));
            updateCanBackfill();
            // Tell everybody that is spectating that a spot has opened up for them.
            for (Player joiningPlayer : game.getBukkitSpectators()){
                sendCanJoinMessage(joiningPlayer, absentPlayer);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        offlinePlayers.remove(event.getPlayer());
        updateCanBackfill();
        if (canBackfill && !GameManager.getGame().isParticipating(event.getPlayer())) {
            List<Player> keysAsArray = new ArrayList<>(offlinePlayers.keySet());
            sendCanJoinMessage(event.getPlayer(), keysAsArray.get(0));
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
     * @return The player that corresponds with the IGN supplied: can be null
     */
    private static Player getOfflineFromString(String name) {
        for (Player player : offlinePlayers.keySet()) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    private static void sendCanJoinMessage(Player joiningPlayer, Player absentPlayer) {
        joiningPlayer.sendMessage(String.format("%s%sA spot has opened up for you on the %s team. \nType \"/accept %s\" to join!",
                ChatColor.LIGHT_PURPLE,
                ChatColor.BOLD,
                GameManager.getGame().getTeam(absentPlayer).getName(),
                absentPlayer.getName()));
    }
}
