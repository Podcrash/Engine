package com.podcrash.api.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

public class TellCommand extends BukkitCommand {

    public TellCommand() {
        super("tell",
                "Currently unused messaging system.",
                "/tell",
                Arrays.asList(
                        "message",
                        "msg"
                ));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        //TODO make this better
        if (sender.hasPermission("invicta.mute")) {
            sender.sendMessage(String.format("%sInvicta> %sNice try, you are still muted.", ChatColor.BLUE, ChatColor.GRAY));
        } else {
            sender.sendMessage(String.format("%sInvicta> %sThe /tell command is currently disabled.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
