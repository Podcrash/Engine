package com.podcrash.api.mc.game.scoreboard;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameType;
import com.podcrash.api.mc.scoreboard.CustomScoreboard;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameLobbyScoreboard extends CustomScoreboard{

    private Game game;
    private boolean running;

    public GameLobbyScoreboard(Game game) {
        super(15);
        this.game = game;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
        TimeHandler.repeatedTime(5, 0, new TimeResource() {
            @Override
            public void task() {
                updateLobbyScoreboard();
                for (Player player : game.getBukkitPlayers()){
                    player.setScoreboard(getBoard());
                }
            }

            @Override
            public boolean cancel() {
                return game.isOngoing();
            }

            @Override
            public void cleanup() {
                for (Player player : game.getBukkitPlayers()){
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
                running = false;
            }
        });
    }

    private void updateLobbyScoreboard() {
        List<String> lines = new ArrayList<String>();

        lines.add("");
        lines.add(game.getName());
        lines.add(game.getMode());
        lines.add("");
        lines.add("&b&lServer");
        lines.add("&f" + Bukkit.getServerName());
        lines.add("");
        lines.add("&e&lPlayers");
        lines.add("&f" + game.getPlayerCount() + "/" + game.getCapacity());
        lines.add("");
        lines.add("&d&lMap");
        lines.add("Map Name");
        lines.add("");
        lines.add("&a&lStatus");
        lines.add("Waiting for players...");

        setLines(lines);
    }
}
