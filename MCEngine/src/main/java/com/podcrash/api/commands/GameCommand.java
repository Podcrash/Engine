package com.podcrash.api.commands;

import com.podcrash.api.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class GameCommand extends BukkitCommand {
    public GameCommand() {
        super("game",
                "Switch to another game",
                "/game <name>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("List of available games: " + GameManager.getPossibleGames().toString());
            return true;
        }
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            if (GameManager.getPossibleGames().contains(arg)) {
                GameManager.createGame(arg);
            }else commandSender.sendMessage(arg + " is not a game!");
            return true;
        }
        return true;
    }


}
