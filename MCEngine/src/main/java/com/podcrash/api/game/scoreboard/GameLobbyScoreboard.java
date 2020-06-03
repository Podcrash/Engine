package com.podcrash.api.game.scoreboard;

import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameState;
import com.podcrash.api.scoreboard.CustomScoreboard;
import com.podcrash.api.scoreboard.ScoreboardInput;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameLobbyScoreboard extends ScoreboardInput {

    private final Game game;

    public GameLobbyScoreboard(Game game, CustomScoreboard scoreboard) {
        super(scoreboard);
        this.game = game;
    }

    @Override
    public void update() {
        List<String> lines = new ArrayList<>();

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

    @Override
    public boolean cancel() {
        return game.getGameState() == GameState.STARTED;
    }
}
