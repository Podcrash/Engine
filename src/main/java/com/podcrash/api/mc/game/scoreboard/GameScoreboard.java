package com.podcrash.api.mc.game.scoreboard;

import com.podcrash.api.mc.game.GameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public abstract class GameScoreboard {
    private final ScoreboardManager scoreboardManager;
    private Scoreboard board;
    private Objective objective;
    private int gameId;

    public GameScoreboard(int gameId, GameType gtype) {
        this.gameId = gameId;
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    /**
     * Create a new bukkit board as well assign an objective.
     * @return Bukkit scoreboard
     */
    public Scoreboard createBoard() {
        board = scoreboardManager.getNewScoreboard();
        makeObjective();
        return board;
    }

    /**
     * Make a new objective
     */
    public void makeObjective() {
        Objective obj;
        if((obj = board.getObjective(Integer.toString(gameId))) != null) obj.unregister();
        objective = board.registerNewObjective(Integer.toString(gameId), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.LIGHT_PURPLE + "Champions");
    }

    /**
     * First time a scoreboard is created.
     */
    public abstract void setupScoreboard();

    /**
     * Used to turn the strings into a scoreboarda
     * @param strings
     */
    public abstract void convertScoreboard(String[] strings);

    /**
     * If there are values to be changed, update them.
     */
    public abstract void update();

    public Scoreboard getBoard() {
        return board;
    }

    public void setScoreBoard(Player p) {
        p.setScoreboard(this.board);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Scoreboard getBukkitBoard() {
        return board;
    }

    public Objective getObjective() {
        return objective;
    }

    public int getGameId() {
        return this.gameId;
    }
}
