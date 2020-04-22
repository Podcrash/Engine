package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DecreaseMaxPlayersCommand extends CommandBase{

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers();
        int target = currMax - 1;

        int currMaxForSingleTeam = 0;
        for (GTeam team : game.getTeams()) {
            if (team.getPlayers().size() > currMaxForSingleTeam) currMaxForSingleTeam = team.getPlayers().size();
        }

        //System.out.println(currMax + " " + currMaxForSingleTeam + " " + target + " " + currMaxForSingleTeam * game.getTeams().size());
        //If we can even decrease at all
        if (target >= currMaxForSingleTeam * game.getTeams().size() && target >= game.getMinPlayers()) {
            game.setMaxPlayers(target);
            //If we need to lower the team maxes by 1
            if (currMaxForSingleTeam * game.getTeams().size() < target - game.getTeams().size()) {
                for (GTeam team : game.getTeams()) {
                    team.setMaxPlayers(team.getMaxPlayers() - 1);
                }
            }
            return true;
        }
        return false;

    }
}
