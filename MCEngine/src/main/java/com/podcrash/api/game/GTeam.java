package com.podcrash.api.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generic Teams Class.
 *
 * @author JJCunningCreeper
 */

public class GTeam {

    private TeamEnum teamEnum;
    private String name;
    private int min;
    private int max;
    private final List<UUID> players;
    private List<Location> spawnpoints;
    private final AtomicInteger score;

    /**
     * Constructor for a Team.
     */
    public GTeam(TeamEnum teamEnum, int min, int max, List<Location> spawnpoints) {
        this.teamEnum = teamEnum;
        this.name = teamEnum.getName();
        this.players = new ArrayList<>();
        this.min = min;
        this.max = max;
        this.spawnpoints = spawnpoints;
        this.score = new AtomicInteger(0);
    }

    /**
     * @return The ID of the team.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team.
     * @param name The name of the team.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The team enum.
     */
    public TeamEnum getTeamEnum() {
        return teamEnum;
    }

    /**
     * @param teamEnum The team enum.
     */
    public void setTeamEnum(TeamEnum teamEnum) {
        this.teamEnum = teamEnum;
    }

    /**
     * @return The minimum number of players for the team.
     */
    public int getMinPlayers() { return min; }

    /**
     * Set the minimum players for a team.
     * @param min The minimum number of players.
     */
    public void setMinPlayers(int min) { this.min = min; }

    /**
     * @return The maximum number of players for the team.
     */
    public int getMaxPlayers() { return max; }

    /**
     * Set the maximum players for a team.
     * @param max The maximum number of players.
     */
    public void setMaxPlayers(int max) { this.max = max; }

    /**
     * @return List of players on the team.
     */
    public List<UUID> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * @return Size of the team (number of players on the team).
     */
    public int teamSize() {
        return players.size();
    }

    /**
     * @return If the team is empty (0 players).
     */
    public boolean isEmpty() {
        return (teamSize() == 0);
    }

    /**
     * Checks if a player is on a team.
     * @param uuid The UUID of the player.
     * @return If the player is on the team.
     */
    public boolean isPlayerOnTeam(UUID uuid) {
        return players.contains(uuid);
    }

    /**
     * Checks if a player is on a team.
     * @param player The player.
     * @return If the player is on the team.
     */
    public boolean isPlayerOnTeam(Player player) {
        return players.contains(player.getUniqueId());
    }

    /**
     * Add a player with a UUID to the team.
     * @param uuid The UUID of the player.
     */
    public void addToTeam(UUID uuid) {
        players.add(uuid);
    }

    /**
     * Add a player to the team.
     * @param player The player.
     */
    public void addToTeam(Player player) {
        players.add(player.getUniqueId());
    }

    /**
     * Add a list of players to the team.
     * @param players A list of players.
     */
    public void addToTeam(List<UUID> players) {
        this.players.addAll(players);
    }

    /**
     * Remove a player with a UUID from the team.
     * @param uuid The UUID of the player to remove.
     */
    public void removeFromTeam(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Remove a player from the team.
     * @param player The player.
     */
    public void removeFromTeam(Player player) {
        players.remove(player.getUniqueId());
    }

    /**
     * Clear the team.
     */
    public void clearTeam() {
        players.clear();
    }

    /**
     * @return The team score.
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Set the team's score.
     * @param new_score The score to set.
     */
    public void setScore(int new_score) {
        score.set(new_score);
    }

    /**
     * @return The team's spawnpoints.
     */
    public List<Location> getSpawns() {
        return spawnpoints;
    }

    /**
     * Return a spawnpoint at a given index.
     * @param index The index of the spawnpoint.
     * @return The spawnpoint.
     */
    public Location getSpawn(int index) {
        return spawnpoints.get(index);
    }

    /**
     * Get the spawn of a player based off their index.
     * @param player The player.
     * @return The spawnpoint location.
     */
    public Location getSpawn(Player player) {
        if (!isPlayerOnTeam(player))
            return null;
        return spawnpoints.get(players.indexOf(player.getUniqueId()));
    }

    /**
     * Set the spawns for the team.
     * @param spawnpoints The spawns.
     */
    public void setSpawns(List<Location> spawnpoints) {
        this.spawnpoints = spawnpoints;
    }

    /**
     * @return "Nice Looking" String for the team.
     */
    public String niceLooking() {
        StringBuilder result = new StringBuilder(teamEnum.getChatColor() + "" + ChatColor.BOLD + teamEnum.getName() + " Team: ");
        result.append(ChatColor.RESET);
        for (UUID uuid : players) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            result.append(p.getName());
            result.append(' ');
        }
        return result.toString();
    }

    public List<Player> getBukkitPlayers() {
        List<Player> players = new ArrayList<>();
        getPlayers().forEach(uuid -> players.add(Bukkit.getPlayer(uuid)));
        return players;
    }
    /**
     * Lazy method to spawn in ALL the players into their spawns
     */
    public final void allSpawn() {
        List<Player> players = getBukkitPlayers();
        int locCursor = 0;
        int spawnSize = spawnpoints.size();
        for(Player player : players) {
            if (locCursor >= spawnSize) locCursor = 0;//if more players than spawns
            player.teleport(spawnpoints.get(locCursor));
            locCursor++;
        }
    }
}
