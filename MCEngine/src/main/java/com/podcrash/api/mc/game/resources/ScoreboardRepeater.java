package com.podcrash.api.mc.game.resources;

import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardRepeater extends GameResource {
    private GameScoreboard scoreboard;
    private ScoreboardRepeater(int gameID, int ticks, int delayTicks) {
        super(gameID, ticks, delayTicks);
    }

    public ScoreboardRepeater(int gameID) {
        //every 10 ticks, I'm not sure if a scoreboard render is going to lag the client but it doesn't
        //need to be refreshed every 1/20th of a second
        this(gameID, 5, 0);
        this.scoreboard = getGame().getGameScoreboard();
    }

    /**
     * Remake the scoreboard and show it to the players
     */
    private void update() {
        this.scoreboard.update();
        for (Player player : getGame().getBukkitPlayers()){
            player.setScoreboard(scoreboard.getBoard());
        }
    }

    @Override
    public void task() {
        update();
    }

    /**
     * Clear the old scoreboard from the players
     */
    @Override
    public void cleanup() {
        for (Player player : getGame().getBukkitPlayers()){
            //clear scoreboard
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        clear();
    }
}
