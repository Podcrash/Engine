package com.podcrash.api.scoreboard;

import com.podcrash.api.time.resources.TimeResource;

import java.util.List;

public abstract class ScoreboardInput implements TimeResource {
    private final CustomScoreboard scoreboard;

    public ScoreboardInput(CustomScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
    public abstract void update();

    @Override
    public void task() {
        update();
    }

    @Override
    public void cleanup() {

    }

    protected void changeLine(int i, String newLine) {
        scoreboard.setLine(i - 1, newLine);
    }

    protected List<String> getLines() {
        return scoreboard.getLines();
    }
    protected void setLines(List<String> lines) {
        scoreboard.setLines(lines);
    }

    protected void setLine(int i, String line) {
        scoreboard.setLine(i, line);
    }
}
