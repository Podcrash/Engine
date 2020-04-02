package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("invicta.host")) {
            if (!(sender instanceof Player)) return false;
            if(!GameManager.getGame().isOngoing()) {
                sender.sendMessage(String.format("%sInvicta> %sThe game has not started yet.", ChatColor.BLUE, ChatColor.GRAY));
                return true;
            }
            Game game = GameManager.getGame();
            GameManager.endGame(game);
            return true;
        } else {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
