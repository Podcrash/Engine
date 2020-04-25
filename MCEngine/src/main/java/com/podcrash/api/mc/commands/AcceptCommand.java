package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.listeners.BackfillListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            if (BackfillListener.replaceOfflineWithSpectator((Player) sender, args[0])) {
                //sender.sendMessage("sucess");
            }
        } else if(args.length == 0 && sender instanceof Player) {
            Game game = GameManager.getGame();
            for (GTeam team : game.getTeams()) {
                if (team.getPlayers().size() < team.getMaxPlayers()) {
                    if (BackfillListener.backfillSpectatorIntoGame((Player) sender, team.getTeamEnum())) {

                    } else

                    break;
                }
            }

        }
        return true;
    }
}
