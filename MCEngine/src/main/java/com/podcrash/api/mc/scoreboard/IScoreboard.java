package com.podcrash.api.mc.scoreboard;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface IScoreboard {
    /**
     * Convert/assign an array of strings to the scoreboard, starting from the top.
     * @param lines Array of lines to assign starting from the top of the scoreboard.
     */
    void setLines(String[] lines);

    /**
     * Convert/assign a list of strings to the scoreboard, starting from the top.
     * @param lines List of lines to assign starting from the top of the scoreboard.
     */
    void setLines(List<String> lines);

    /**
     * Destroy the scoreboard.
     * Cancels the timer and unregisters objectives.
     */
    void destroyScoreboard();

    /**
     * Get all the lines from the scoreboard
     * @see #getLine(int)
     * @return the display lines as seen from the client
     */
    List<String> getLines();

    /**
     * Get the prefix + suffix of a line on a scoreboard
     * @param i team id
     * @return the display line as seen from the client
     */
    String getLine(int i);

    /**
     * Appropriately sets a line of the scoreboard to the provided string.
     * Depending on the length of the string, it may be shortened.
     * TODO: This needs to be heavily tested and possibly refactored.
     * @param i The line number as indicated by the score number.
     * @param line The line to set.
     */
    void setLine(int i, String line);

    /**
     * Sets a line of the scoreboard given a prefix and a suffix.
     * @param i The line number as indicated by the score number.
     * @param prefix The prefix string to set. Must be 16 characters or less.
     * @param suffix The suffix string to set. Must be 16 characters or less.
     * @return
     */
    boolean setLine(int i, String prefix, String suffix);

    /**
     * Sets the prefix (first part) of a line number.
     * @param line The line number as indicated by the score number.
     * @param prefix The prefix string to set. Must be 16 characters or less.
     * @return Whether the assignment was successful.
     */
    boolean setPrefix(int line, String prefix);

    /**
     * Sets the suffix (latter part) of a line number.
     * @param line The line number as indicated by the score number.
     * @param suffix The suffix string to set. Must be 16 characters or less.
     * @return Whether the assignment was successful.
     */
    boolean setSuffix(int line, String suffix);

    /**
     * @return The custom scoreboard.
     */
    Scoreboard getBoard();

    /**
     * @return The custom scoreboard objective.
     */
    Objective getObjective();

    /**
     * @return The size of the scoreboard.
     */
    int getSize();
}
