package com.podcrash.api.commands.helpers;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.game.lobby.GameLobbyTimer;
import org.bukkit.entity.Player;

public class PPLCommands {

    public static void increaseMaxPlayers() {
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers() + 1;
        int possibleMax = game.getTeam(0).getMaxPlayers() * game.getTeams().size();
        game.setMaxPlayers(currMax);
        if (currMax > possibleMax) {
            for (GTeam team : game.getTeams()) {
                team.setMaxPlayers(team.getMaxPlayers() + 1);
            }
        }
    }

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


}
