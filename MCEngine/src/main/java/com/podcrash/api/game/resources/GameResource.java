package com.podcrash.api.game.resources;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.plugin.PodcrashSpigot;

public abstract class GameResource {
    protected final Game game;
    private final int gameID;

    public GameResource(int gameID) {
        this.gameID = gameID;
        this.game = GameManager.getGame();
    }

    public int getGameID() {
        return gameID;
    }

    public abstract void init();
    public abstract void stop();

    protected void log(String msg){
        PodcrashSpigot.getInstance().getLogger().info(String.format("%s: %s", this.getClass().getSimpleName(), msg));
    }
}
