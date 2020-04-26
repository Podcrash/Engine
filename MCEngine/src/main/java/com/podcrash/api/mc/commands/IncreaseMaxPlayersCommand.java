package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class IncreaseMaxPlayersCommand extends BukkitCommand {
    public IncreaseMaxPlayersCommand() {
        super("increase",
                "Increase the maximum player count within a PPL",
                "/increase",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) return true;
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
