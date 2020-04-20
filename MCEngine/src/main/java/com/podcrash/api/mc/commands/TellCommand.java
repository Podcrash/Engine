package com.podcrash.api.mc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TellCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //TODO make this better
        if (sender.hasPermission("invicta.mute")) {
            sender.sendMessage(String.format("%sInvicta> %sNice try, you are still muted.", ChatColor.BLUE, ChatColor.GRAY));
        } else {
            sender.sendMessage(String.format("%sInvicta> %sThe /tell command is currently disabled.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
