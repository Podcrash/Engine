package com.podcrash.api.mc.game;

import org.bukkit.Bukkit;

import java.util.*;

//TODO maybe delete this class because it doesnt do anything
public class GamePlayerTeamAssign {

    public static boolean sortPlayers(Game game, boolean balanced) {
        // Set the variables.
        List<GTeam> teams = game.getTeams();
        List<UUID> extra = new ArrayList<UUID>(game.getParticipantsNoTeam());
        int teamCount = teams.size();
        int participantCount = game.size();
        int cap = (participantCount/teamCount);
        // If balanced, assign overflow to extras.
        if (balanced) {
            for (GTeam team : teams) {
                int teamCap;
                // Determine the cap for the team.
                if (cap > team.getMaxPlayers()) {
                    teamCap = team.getMaxPlayers();
                } else {
                    teamCap = cap;
                }
                // If the team size is greater than teamCap, assign overflow to extras.
                if (team.teamSize() > teamCap) {
                    extra.addAll(team.getPlayers().subList(teamCap, team.teamSize()));
                    team.getPlayers().removeAll(team.getPlayers().subList(teamCap, team.teamSize()));
                }
            }
        }
        // Assign the extras to a team (in an attempt to balance).
        Iterator<UUID> iterator = extra.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            // Get all teams that have space left. If there are no teams, break.
            List<GTeam> available = getNonFullTeams(game);
            if (available.isEmpty()) { break; }
            // Get the team with the least number of players.
            // Add player to the team and remove from extras.
            GTeam min = available.get(0);
            for (GTeam team : available) {
                if (team.teamSize() < min.teamSize()) {
                    min = team;
                }
            }
            game.joinTeam(Bukkit.getPlayer(uuid), min.getTeamEnum(), false);
            iterator.remove();
        }
        // For those who aren't on a team by the end, set to spectator.
        for (UUID uuid : extra) {
            game.addSpectator(Bukkit.getServer().getPlayer(uuid));
        }
        return true;
    }

    private static List<GTeam> getNonFullTeams(Game game) {
        List<GTeam> result = new ArrayList<GTeam>();
        for (GTeam team : game.getTeams()) {
            if (team.teamSize() < team.getMaxPlayers()) {
                result.add(team);
            }
        }
        return result;
    }
}
