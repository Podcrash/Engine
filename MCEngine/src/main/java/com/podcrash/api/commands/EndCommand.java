package com.podcrash.api.commands;

import com.podcrash.api.commands.helpers.GameCommands;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EndCommand extends BukkitCommand {

    public EndCommand() {
        super("endgame",
                "End a game.",
                "/endgame",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host") || !(sender instanceof Player)) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        GameCommands.endGame((Player) sender);
        return true;
    }
}
