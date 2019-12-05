package com.podcrash.api.mc.game.scoreboard;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameType;
import com.podcrash.api.mc.scoreboard.CustomScoreboard;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class GameLobbyScoreboard extends CustomScoreboard {

    private int gameId;

    public GameLobbyScoreboard(int size, int gameId, GameType gtype) {
        super(size);
        this.gameId = gameId;
    }

    public void createLobbyScoreboard(Game game) {
        List<String> lines = new ArrayList<String>();

        lines.add("");
        lines.add(game.getName());
        //lines.add(game.getMode());
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
