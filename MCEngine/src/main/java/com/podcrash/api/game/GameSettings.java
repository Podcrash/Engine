package com.podcrash.api.game;

import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    private int minPlayers;
    private int maxPlayers;

    private List<GTeam> teams;

    public GameSettings(TeamSettings settings, List<GTeam> teams) {
        this.minPlayers = 0;
        this.maxPlayers = 0;

        for (TeamEnum team : settings.getTeamColors()) {
            this.minPlayers += settings.getMin();
            this.maxPlayers += settings.getMax();
        }
        this.teams = teams;
    }

    public GameSettings(GameSettings other) {
        this.minPlayers = other.minPlayers;
        this.maxPlayers = other.maxPlayers;
        teams = new ArrayList<>();
        for (GTeam team : other.getTeams()) {
            teams.add(new GTeam(team.getTeamEnum(), team.getMinPlayers(), team.getMaxPlayers(), team.getSpawns()));
        }

    }

    /**
     * Get the minimum number of players possible for the game
     * @return minimum number of players
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Get the maximum number of players possible for the game
     * @return maximum number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the max players for a game
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Set the min players for a game
     * @param minPlayers
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public List<GTeam> getTeams() {
        return teams;
    }
}
