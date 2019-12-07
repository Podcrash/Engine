package com.podcrash.api.mc.game;

import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.WinObjective;

import java.util.List;

public interface IGame {
    List<WinObjective> getWinObjectives();
    List<ItemObjective> getItemObjectives();

    /**
     * Update the score values.
     * @param team
     * @param score the increment to add by
     */
    void increment(TeamEnum team, int score);
}
