package com.podcrash.api.mc.game.resources;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.plugin.Pluginizer;

/**
 * This is used as helpers for games that have started.
 */
public abstract class GameResource implements TimeResource {
    private Game game;
    private int gameID;

    private int ticks;
    private int delayTicks;

    @Override //default: feel free to override
    public boolean cancel() {
        return (getGame() == null) || getGame().getGameState() == GameState.LOBBY;
    }

    public GameResource(int gameID, int ticks, int delayTicks){
        this.gameID = gameID;
        this.game = GameManager.getGame();
        this.ticks = ticks;
        this.delayTicks = delayTicks;
    }
    public GameResource(int gameID) {
        this(gameID, 1, 0);
    }

    public int getGameID() {
        return gameID;
    }
    public Game getGame(){
        return game;
    }

    public int getTicks() {
        return ticks;
    }
    public int getDelayTicks() {
        return delayTicks;
    }

    protected final void log(String msg){
        Pluginizer.getSpigotPlugin().getLogger().info(String.format("%s: %s", this.getClass().getSimpleName(), msg));
    }

    protected void clear() {
        this.gameID = -1;
        this.game = null;
        this.ticks = -1;
        this.delayTicks = -1;
    }
}
