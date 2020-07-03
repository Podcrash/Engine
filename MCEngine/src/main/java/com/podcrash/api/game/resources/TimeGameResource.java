package com.podcrash.api.game.resources;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.time.resources.TimeResource;

/**
 * This is used as helpers for games that have started.
 */
public abstract class TimeGameResource extends GameResource implements TimeResource {
    private int ticks;
    private int delayTicks;

    @Override //default: feel free to override
    public boolean cancel() {
        return (game == null) || game.getGameState() == GameState.LOBBY;
    }

    @Override
    public void stop() {
        unregister();
    }

    public TimeGameResource(int gameID, int ticks, int delayTicks){
        super(gameID);
        this.ticks = ticks;
        this.delayTicks = delayTicks;
    }
    public TimeGameResource(int gameID) {
        this(gameID, 1, 0);
    }

    @Override
    public void init() {
        this.run(ticks, delayTicks);
    }

    protected void clear() {
        this.ticks = -1;
        this.delayTicks = -1;
    }
}
