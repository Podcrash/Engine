package com.podcrash.api.mc.scoreboard;

import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * The Sidebar Scoreboard Title Animator.
 * TODO: Refactoring required. Find an algorithm for the animation so the code isn't such hot garbage lol.
 *
 * @author JJCunningCreeper
 */

public class ScoreboardTitleAnimator implements TimeResource {

    private int i;
    private final CustomScoreboard scoreboard;
    private final String title;
    private final String primary_color;
    private final String secondary_color;
    private final int shine_begin;
    private final int shine_bdr_begin;
    private final int flash_begin;
    private final int still_begin;
    private final int period;

    /**
     * Constructor for the Scoreboard Title Animator.
     * @param scoreboard The custom scoreboard
     */
    public ScoreboardTitleAnimator (CustomScoreboard scoreboard, String title, String primary, String secondary) {
        this.i = 0;
        this.scoreboard = scoreboard;
        // If the title is greater than 9 characters, take the first 9 characters.
        if (title.length() > 9) {
            title = title.substring(0, 9);
        }
        this.title = title;
        this.primary_color = primary;
        this.secondary_color = secondary;
        // Animation Stages
        this.shine_begin = 11;
        this.shine_bdr_begin = this.shine_begin + (this.title.length() + 3);
        this.flash_begin = this.shine_bdr_begin + (this.title.length() + 5);
        this.still_begin = this.flash_begin + 3;
        this.period = 50;
    }

    @Override
    public void task() {
        if (i <= period) {
            if (i < shine_begin)
                decorateAndDisplayName(titlePrimaryFill());
            else if (i < shine_bdr_begin)
                decorateAndDisplayName(shineStage(i - shine_begin, false));
            else if (i < flash_begin)
                decorateAndDisplayName(shineStage(i - shine_bdr_begin, true));
            else if (i < still_begin)
                decorateAndDisplayName(titleFlash(i - flash_begin));
            else
                decorateAndDisplayName(titlePrimaryFill());
            i++;
        } else {
            i = 0;
        }
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }

    /**
     * The title state at a given time.
     * TODO: Use Linked Lists
     * @param i The relative time index.
     * @param bidirectional Whether the shine should occur from both sides.
     * @return The title at the time.
     */
    private String shineStage(int i, boolean bidirectional) {
        // The result string.
        StringBuilder result = new StringBuilder();
        // Shine width
        int shine_width;
        if (bidirectional)
            shine_width = 4;
        else
            shine_width = 3;
        // The characters of the title.
        String[] chars = title.split("");
        // The formatting codes for each character.
        List<String> codes = new ArrayList<>();
        // The shine
        for (int pos = 0; pos < title.length(); pos++) {
            // From left to right.
            if ((pos < i) && (pos >= (i - shine_width)))
                codes.add(secondary_color);
            else
                codes.add(primary_color);
            // From right to left if bi-directional
            if (bidirectional && (pos >= title.length() - i) && (pos < title.length() + shine_width - i) && (codes.get(pos).equals(primary_color)))
                codes.set(pos, secondary_color);
        }
        // Populate the result string.
        String prev = "";
        for (int j = 0; j < title.length(); j++) {
            if (!codes.get(j).equals(prev)) {
                prev = codes.get(j);
                result.append(codes.get(j)).append(chars[j]);
            } else {
                result.append(chars[j]);
            }
        }
        return result.toString();
    }

    /**
     * The title flash at a given time.
     * @param i Time index. If odd, primary flash. Else, secondary flash.
     * @return The title at the time.
     */
    private String titleFlash(int i) {
        if (i % 2 == 0)
            return titleSecondaryFill();
        return titlePrimaryFill();
    }

    /**
     * @return Completely primary colored title string.
     */
    private String titlePrimaryFill() {
        return primary_color + this.title;
    }

    /**
     * @return Completely secondary colored title string.
     */
    private String titleSecondaryFill() {
        return secondary_color + this.title;
    }

    /**
     * Decorates the title (Adds green decorations on both ends) and sets the display name of the scoreboard.
     * @param title The final string to display.
     */
    private void decorateAndDisplayName(String title) {
        String final_title = ChatUtil.chat("&a-= " + title + "&a=-");
        scoreboard.getObjective().setDisplayName(ChatUtil.chat(final_title));
    }
}
