package com.podcrash.api.game.scoreboard;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameType;
import com.podcrash.api.scoreboard.CustomScoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * The Game Scoreboard.
 *
 * @author RainDance
 * (edited by JJCunningCreeper)
 */

public abstract class GameScoreboard extends CustomScoreboard {

    private final int gameId;

    /**
     * Constructor for the Game Scoreboard.
     * @param size The number of lines on the scoreboard.
     * @param gameId The Game ID.
     * @param gtype The Game Type (Idk what this is).
     */
    public GameScoreboard(int size, int gameId, GameType gtype) {
        super(size);
        this.gameId = gameId;
    }

    /**
     * Create a game scoreboard with a name & mode string, as well as the following lines.
     * Maximum 15 lines total (following_lines can only be of max 11 lines).
     * @param name The game name (with formatting).
     * @param mode The game mode (with formatting).
     * @param lines List of lines to follow (Maximum 11).
     */
    public void createGameScoreboard(String name, String mode, List<String> lines) {
        // Create the final set of lines for the scoreboard.
        List<String> result = createGameLines(name, mode, lines);
        // Convert lines to scoreboard.
        setLines(result);
    }

    /**
     * If there are values to be changed, update them.
     */
    public abstract void update();

    public abstract void startScoreboardTimer();

    /**
     * @return The Game ID.
     */
    public int getGameId() {
        return this.gameId;
    }

    public Game getGame() {
        return GameManager.getGame();
    }
    /**
     * Given a name, mode and lines, createScoreboard the final list of lines for the game scoreboard.
     * @param name The game name.
     * @param mode The game mode.
     * @param lines The list of lines to follow.
     * @return The final lines of the game scoreboard.
     */
    private List<String> createGameLines(String name, String mode, List<String> lines) {
        List<String> result = new ArrayList<>();
        result.add(name);
        result.add(mode);
        result.add("");
        result.addAll(lines);
        return result;
    }
}
