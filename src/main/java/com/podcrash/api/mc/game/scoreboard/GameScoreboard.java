package com.podcrash.api.mc.game.scoreboard;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameType;
import com.podcrash.api.mc.scoreboard.CustomScoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * The Game Scoreboard.
 *
 * @author RainDance
 * (edited by JJCunningCreeper)
 */

public abstract class GameScoreboard extends CustomScoreboard {

    private int gameId;

    /**
     * Constructor for the Game Scoreboard.
     * @param gameId The Game ID.
     * @param gtype The Game Type (Idk what this is).
     */
    public GameScoreboard(int gameId, GameType gtype) {
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
        // Determine the size of the board by the number of lines. Maximum 15 lines total.
        int size = 4 + lines.size();
        if (size > 15) {
            size = 15;
        }
        // Create the board.
        createBoard(size);
        // Create the final set of lines for the scoreboard.
        List<String> result = createGameLines(name, mode, lines);
        // Convert lines to scoreboard.
        convertScoreboard(result);
    }

    /**
     * Create a game scoreboard with a name & mode string, as well as the following lines.
     * Maximum 15 lines total (following_lines can only be of max 11 lines).
     * @param board_size The total size of the board (name + mode + following_lines). Minimum 4. Maximum 15.
     * @param name The game name (with formatting).
     * @param mode The game mode (with formatting).
     * @param lines List of lines to follow (Maximum 11).
     */
    public void createGameScoreboard(int board_size, String name, String mode, List<String> lines) {
        // Check the board size.
        if (board_size < 4 || board_size > 15) {
            board_size = 15;
        }
        createBoard(board_size);
        // Create the final set of lines for the scoreboard.
        List<String> result = createGameLines(name, mode, lines);
        // Convert lines to scoreboard.
        convertScoreboard(result);
    }

    /**
     * If there are values to be changed, update them.
     */
    public abstract void update();

    /**
     * @return The Game ID.
     */
    public int getGameId() {
        return this.gameId;
    }

    /**
     * Given a name, mode and lines, create the final list of lines for the game scoreboard.
     * @param name The game name.
     * @param mode The game mode.
     * @param lines The list of lines to follow.
     * @return The final lines of the game scoreboard.
     */
    private List<String> createGameLines(String name, String mode, List<String> lines) {
        List<String> result = new ArrayList<String>();
        result.add("");
        result.add(name);
        result.add(mode);
        result.add("");
        result.addAll(lines);
        return result;
    }

    protected Game getGame() {
        return GameManager.getGame();
    }
}
