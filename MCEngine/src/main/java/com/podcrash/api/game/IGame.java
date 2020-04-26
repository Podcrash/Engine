package com.podcrash.api.game;

import com.podcrash.api.game.objects.ItemObjective;
import com.podcrash.api.game.objects.WinObjective;

import java.util.List;

public interface IGame {
    List<WinObjective> getWinObjectives();
    List<ItemObjective> getItemObjectives();

    /**
     * Update the score values.
     * @param team Team whose scores should be updated
     * @param score the increment to add by
     */
    void increment(TeamEnum team, int score);
}
