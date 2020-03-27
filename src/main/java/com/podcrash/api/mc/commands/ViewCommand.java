package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ViewCommand extends CommandBase {

    //TODO: change the Game.toString() so that it is applicable for games w/ more than two teams

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(GameManager.getGame() != null) {
            sender.sendMessage(GameManager.getGame().toString());
        }else sender.sendMessage(String.format(
                "%sInvicta> %sA game has not been created yet.",
                ChatColor.BLUE,
                ChatColor.GRAY));
        return true;
    }
}
