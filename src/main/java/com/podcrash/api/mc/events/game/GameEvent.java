package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public abstract class GameEvent extends Event {

    protected List<Player> players;
    protected List<Player> redTeam;
    protected List<Player> blueTeam;

    protected List<Location> redSpawn;
    protected List<Location> blueSpawn;

    protected Game game;

    protected String message;

    public GameEvent(Game game, String message) {
        this.game = game;
        this.players = game.getPlayers();
        this.redTeam = game.getRedTeam();
        this.blueTeam = game.getBlueTeam();
        this.redSpawn = game.getRedSpawn();
        this.blueSpawn = game.getBlueSpawn();
        this.message = message;
    }

    public Game getGame() {
        return this.game;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return game.getId();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean contains(Player p) {
        return players.contains(p);
    }

    public int getPlayerCount() {
        return getPlayers().size();
    }

    public List<Player> getTeam(String teamcolor) {
        if (teamcolor.toLowerCase().equals("red")) return getRedTeam();
        else if (teamcolor.toLowerCase().equals("blue")) return getBlueTeam();
        throw new IllegalArgumentException("teamcolor must be red or blue, not " + teamcolor);
    }

    public List<Player> getBlueTeam() {
        return blueTeam;
    }

    public List<Player> getRedTeam() {
        return redTeam;
    }

    public String getName() {
        return game.getName();
    }

    public int getMaxPlayers() {
        return game.getMaxPlayers();
    }

    public List<Location> getRedSpawn() {
        return redSpawn;
    }

    public List<Location> getBlueSpawn() {
        return blueSpawn;
    }

}
