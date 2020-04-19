package com.podcrash.api.mc.game.lobby;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GameLobbyTip {
    private static final int tipDelay = 20 * 30;
    private final ChatColor tipHeaderColor = ChatColor.YELLOW;
    private final String tipHeader = "Tip> ";

    private final Game game;

    public GameLobbyTip(Game game) {
        this.game = game;
    }

    public void run() {
        TimeHandler.repeatedTime(tipDelay, 0, new TimeResource() {
            @Override
            public void task() {
                String tip = game.getRandomTip();
                if (tip == null || game.getGameState() != GameState.LOBBY) {
                    return;
                }
                Bukkit.broadcastMessage(tipHeaderColor + tipHeader + ChatColor.RESET + tip);
            }

            @Override
            public boolean cancel() {
                return game.getGameState() != GameState.LOBBY;
            }

            @Override
            public void cleanup() {
            }
        });
    }
}
