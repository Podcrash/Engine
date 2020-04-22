package com.podcrash.api.mc.game.scoreboard;

import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
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
                return game.getGameState() == GameState.STARTED;
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

        lines.add("&c&lGame");
        lines.add(game.getMode());
        lines.add("");
        lines.add("&b&lServer");
        lines.add("&f" + Communicator.getCode());
        lines.add("");
        lines.add("&e&lPlayers");
        lines.add("&f" + game.size() + "/" + game.getMaxPlayers());
        lines.add("");
        lines.add("&d&lMap");
        lines.add(game.getMapName());
        lines.add("");
        lines.add("&a&lStatus");
        lines.add(game.getTimer().getStatus());

        setLines(lines);
    }
}
