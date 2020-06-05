package com.podcrash.api.commands;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.listeners.BackfillListener;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class AcceptCommand extends BukkitCommand {
    public AcceptCommand() {
        super("accept",
                "Accept a player's position in a game",
                "/accept <Player Name>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            BackfillListener.replaceOfflineWithSpectator((Player) sender, args[0]);
        } else if(args.length == 0 && sender instanceof Player) {
            BackfillListener.backfillSpectatorIntoGame((Player) sender);
        }
        return true;
    }
}
