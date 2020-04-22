package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class IncreaseMaxPlayersCommand extends CommandBase{

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers() + 1;
        int possibleMax = game.getTeam(0).getMaxPlayers() * game.getTeams().size();
        game.setMaxPlayers(currMax);
        if (currMax > possibleMax) {
            for (GTeam team : game.getTeams()) {
                team.setMaxPlayers(team.getMaxPlayers() + 1);
            }
        }
        return true;
    }
}
