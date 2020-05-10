package com.podcrash.api.commands.helpers;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.game.lobby.GameLobbyTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

public class PPLCommands {

    /**
     * Increases the maximum capacity of the server by 1
     */
    public static void increaseMaxPlayers() {
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers() + 1;
        //TODO: Get rid of this (temporary maximum)
        if (currMax > 40) return;
        int possibleMax = game.getTeam(0).getMaxPlayers() * game.getTeams().size();
        game.setMaxPlayers(currMax);
        if (currMax > possibleMax) {
            for (GTeam team : game.getTeams()) {
                team.setMaxPlayers(team.getMaxPlayers() + 1);
            }
        }
    }

    /**
     * Decreases the maximum capacity of the server by 1
     */
    public static void decreaseMaxPlayers() {
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers();
        int target = currMax - 1;

        int currMaxForSingleTeam = 0;
        for (GTeam team : game.getTeams()) {
            if (team.getPlayers().size() > currMaxForSingleTeam) currMaxForSingleTeam = team.getPlayers().size();
        }

        //If we can even decrease at all
        if (target >= currMaxForSingleTeam * game.getTeams().size() && target >= game.getMinPlayers()) {
            game.setMaxPlayers(target);

            for (GTeam team : game.getTeams()) {
                team.setMaxPlayers((game.getMaxPlayers() + game.getTeams().size() - 1) / game.getTeams().size());
            }
        }
    }

    /**
     * Toggles the whitelist for the server
     */
    public static void toggleWhitelist() {
        Bukkit.setWhitelist(!Bukkit.hasWhitelist());
    }

    /**
     * Whitelists the player from their name
     * @param name - The name of the player
     * @param shouldWhitelist - if the player should be whitelisted or unwhitelisted
     */
    public static void whitelist(String name, boolean shouldWhitelist) {
        if (shouldWhitelist) {
            Bukkit.getOfflinePlayer(name).setWhitelisted(true);
        } else {
            Bukkit.getOfflinePlayer(name).setWhitelisted(false);
            Player currentPlayer = Bukkit.getPlayer(name);
            if (currentPlayer != null && !currentPlayer.hasPermission("invicta.exempt")) {
                currentPlayer.kickPlayer("Unwhitelisted!");
            }
        }
        Bukkit.reloadWhitelist();
    }

    public static String getStateMsg() {
        return "Whitelist: " + ChatColor.RESET + (Bukkit.hasWhitelist() ? ChatColor.GOLD + "On" : ChatColor.RED + "Off");
    }
}
