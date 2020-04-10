package com.podcrash.api.mc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TellCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("invicta.mute")) {
            sender.sendMessage(String.format("%sInvicta> %sNice try, you are still muted.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        } else {
            sender.sendMessage(String.format("%sInvicta> %sThe /tell command is currently disabled.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
    }
}
