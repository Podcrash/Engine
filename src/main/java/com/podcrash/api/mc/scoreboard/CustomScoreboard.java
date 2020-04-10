package com.podcrash.api.mc.scoreboard;

import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates a custom scoreboard.
 *
 * @author JJCunningCreeper
 */

public class CustomScoreboard implements IScoreboard {

    private Scoreboard scoreboard;
    private Objective objective;
    private int size;
    private ScoreboardTitleAnimator animator;
    private static List<Character> codes =
            Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r');

    /**
     * Constructor for the custom scoreboard.
     * Create or initialize the scoreboard and objective with a size.
     * @param size The size (number of lines) of the scoreboard (Between 1 and 15 inclusive).
     */
    public CustomScoreboard(int size) {
        createBoard(size);
    }

    /**
     * Create or initialize/re-initialize the scoreboard and objective with a size.
     * @param size The size (number of lines) of the scoreboard (Between 1 and 15 inclusive).
     * @return The scoreboard.
     */
    private void createBoard(int size) {
        // Create the scoreboard.
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        // Check for a valid scoreboard size. If not, set size to 15.
        if (size <= 0 || size > 15) {
            size = 15;
        }
        this.size = size;
        makeObjective();
        setupScoreboard();
    }

    private void makeObjective() {
        destroyScoreboard();
        objective = scoreboard.registerNewObjective("gameboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatUtil.chat(""));
        //animateTitle("Control", "&f&l", "&7&l");
    }

    /**
     * The first time a scoreboard is created.
     * Create a scoreboard based off the size with empty lines.
     */
    private void setupScoreboard() {
        for (int i = 1; i <= size; i++) {
            Team team = scoreboard.registerNewTeam(Integer.toString(i));
            String code = codes.get(i - 1).toString();
            String line = ChatUtil.chat("&" + code + "&r");
            team.addEntry(line);
            objective.getScore(line).setScore(i);
        }
    }

    public void setLines(String[] lines) {
        // Assign the lines starting from the top of the scoreboard.
        for (int i = 0; i < size && i < lines.length && lines[i] != null; i++) {
            setLine(size - i, lines[i]);
        }
    }

    public void setLines(List<String> lines) {
        // Assign the lines starting from the top of the scoreboard.
        for (int i = 0; i < size && i < lines.size(); i++) {
            setLine(size - i, lines.get(i));
        }
    }

    public void destroyScoreboard() {
        // Stop the timer.
        TimeHandler.unregister(animator);
        // Unregister the objective.
        if (this.objective != null) {
            this.objective.unregister();
        }
    }

    public List<String> getLines() {
        List<String> entries = new ArrayList<>();
        for(int i = 1; i < size; i++) {
            entries.add(getLine(i));
        }
        return entries;
    }

    public String getLine(int i) {
        Team team = scoreboard.getTeam(Integer.toString(i));
        String prefix = team.getPrefix();
        return prefix + team.getSuffix();
    }

    public void setLine(int line, String string) {
        // Initially empty strings to populate.
        String prefix = "";
        String suffix = "";
        // Split the string up into a prefix and suffix, each at most size characters long, including chat formatting characters.
        // If the string is less than or equal to size characters, then populating the prefix with the string will suffice.
        if (string.length() <= 16) {
            setLine(line, string, suffix);
            return;
        }
        // Otherwise, both prefix and suffix will have to be populated.
        // If the prefix uses a format code, it will have to continue to the suffix.
        String format_code = "";
        // The start and end index of any format code substring.
        int format_start = -1;
        int format_end = -1;
        // Looking for valid color codes starting from the end of the first size characters.
        for (int i = 15; i >= 0; i--) {
            if (string.charAt(i) == '&' && codes.contains(string.charAt(i + 1))) {
                // If a format code is found, indicate the start and end.
                format_start = i;
                format_end = i + 1;
                // Check every 2 characters before the '&' and update the format_start index if there are more
                // format codes preceding.
                for (int j = format_start - 2; j >= 0 && codes.contains(string.charAt(j + 1)); j = j - 2) {
                    format_start = j;
                }
                break;
            }
        }
        // If the prefix (first size characters) doesn't contain a formatting code, then split the string as normal.
        // Both prefix and suffix take a maximum of size characters.
        if (format_start == -1 && format_end == -1) {
            for (int i = 0; i < string.length() && i < 32; i++) {
                if (i < size) {
                    prefix = prefix.concat("" + string.charAt(i));
                } else {
                    suffix = suffix.concat("" + string.charAt(i));
                }
            }
            setLine(line, prefix, suffix);
            return;
        }
        // If there is a format code, then assign it based off the start and end indices.
        format_code = string.substring(format_start, format_end + 1);
        // Assign the prefix and suffix appropriately, limited to size characters each.
        // If the color code stretches across both the prefix and suffix (i.e. for "&a", the '&' is in the prefix and 'a' is in the suffix),
        // then the color code will have to begin at the suffix. (If the format code is at the very end of the prefix or beyond, move the format code to the start of the suffix).
        if (format_end >= 15) {
            prefix = string.substring(0, format_start);
            suffix = (format_code + string.substring(format_end + 1));
        } else {
            prefix = string.substring(0, size);
            suffix = (format_code + string.substring(size));
        }
        // Reduce the suffix length if over size characters
        if (suffix.length() > size) {
            suffix = suffix.substring(0, 15);
        }
        setLine(line, prefix, suffix);
    }

    public boolean setLine(int line, String prefix, String suffix) {
        return (setPrefix(line, prefix) && setSuffix(line, suffix));
    }

    public boolean setPrefix(int line, String prefix) {
        if (prefix.length() > size) {
            return false;
        }
        //Bukkit.broadcastMessage("Line: " + line + " ScoreboardTeam: " + scoreboard.getTeam(Integer.toString(line)) + " Prefix: " + prefix);
        scoreboard.getTeam(Integer.toString(line)).setPrefix(ChatUtil.chat(prefix));
        return true;
    }

    public boolean setSuffix(int line, String suffix) {
        if (suffix.length() > size) {
            return false;
        }
        scoreboard.getTeam(Integer.toString(line)).setSuffix(ChatUtil.chat(suffix));
        return true;
    }


    public Scoreboard getBoard() {
        return this.scoreboard;
    }

    public Objective getObjective() {
        return this.objective;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Animate the scoreboard title.
     */
    private void animateTitle(String title, String primary, String secondary) {
        animator = new ScoreboardTitleAnimator(this, title, primary, secondary);
        animator.run(5, 0);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}