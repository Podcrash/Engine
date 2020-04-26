package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class DecreaseMaxPlayersCommand extends BukkitCommand {
    public DecreaseMaxPlayersCommand() {
        super("decrease",
                "Decrease the maximum player count within a PPL",
                "/decrease",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) return true;
        Game game = GameManager.getGame();
        int currMax = game.getMaxPlayers();
        int target = currMax - 1;

        int currMaxForSingleTeam = 0;
        for (GTeam team : game.getTeams()) {
            if (team.getPlayers().size() > currMaxForSingleTeam) currMaxForSingleTeam = team.getPlayers().size();
        }

        //If we can even decrease at all
        if (target >= currMaxForSingleTeam * game.getTeams().size() && target >= game.getMinPlayers()) {
            game.setMaxPlayers(target);

            for (GTeam team : game.getTeams()) {
                team.setMaxPlayers((game.getMaxPlayers() + game.getTeams().size() - 1) / game.getTeams().size());
            }

            return true;
        }
        return true;

    }
}
