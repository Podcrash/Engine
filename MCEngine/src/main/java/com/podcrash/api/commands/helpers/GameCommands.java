package com.podcrash.api.commands.helpers;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.game.lobby.GameLobbyTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameCommands {

    public static void startGame(Player player, boolean force) {
        if (force) {
            GameManager.startGame();
        }
        if (GameManager.hasPlayer(player)) {
            Game game = GameManager.getGame();
            if (game == null || game.getGameState() == GameState.STARTED) {
                player.sendMessage("Game has started already!");
            }
            GameLobbyTimer timer = game.getTimer();
            if (timer.isRunning()) {
                timer.stop(true);
                player.sendMessage("Timer paused");
            }
            if (game.hasChosenMap()) {
                timer.start();
            } else {
                player.sendMessage("A map has not been set for Game #" + game.getId());
            }
        } else {
            player.sendMessage("You are currently not in a game");
        }
    }

    public static void endGame(Player player) {
        if (GameManager.getGame() == null)
            return;
        if (GameManager.getGame().getGameState() == GameState.LOBBY) {
            if (!GameManager.getGame().getTimer().getStatus().equals(GameManager.getGame().getTimer().getDefaultStatus())) {
                GameManager.getGame().getTimer().stop(false);
                player.sendMessage(String.format("%sInvicta> %sStopping the countdown.", ChatColor.BLUE, ChatColor.GRAY));
                return;
            }
            player.sendMessage(String.format("%sInvicta> %sThe game has not started yet.", ChatColor.BLUE, ChatColor.GRAY));
            return;
        }
        Game game = GameManager.getGame();
        GameManager.endGame(game);
        return;
    }
}
