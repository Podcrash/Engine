package com.podcrash.api.events.game;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.TeamEnum;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;
import java.util.UUID;

/**
 * Game Event
 * TODO: Change this to work with more than one team.
 */
public abstract class GameEvent extends Event {

    protected List<Player> players;

    protected Game game;

    protected String message;

    public GameEvent(Game game, String message) {
        this(game, message, false);
    }
    public GameEvent(Game game, String message, boolean async) {
        super(async);
        this.game = game;
        this.players = game.getBukkitPlayers();
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

    public List<UUID> getTeam(TeamEnum teamEnum) {
        GTeam gteam = game.getTeam(teamEnum);
        if (gteam == null) {
            throw new IllegalArgumentException("teamcolor must be red or blue.");
        }
        return gteam.getPlayers();
    }

    public String getName() {
        return game.getName();
    }

    public int getMaxPlayers() {
        return game.getMaxPlayers();
    }

    public List<UUID> getTeamPlayers(int id) {
        return game.getTeam(id).getPlayers();
    }

    public List<Location> getTeamSpawns(int id) {
        return game.getTeam(id).getSpawns();
    }
}
