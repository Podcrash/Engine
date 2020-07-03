package com.podcrash.api.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

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
        if (sender.hasPermission("invicta.mute")) {
            sender.sendMessage(String.format("%sInvicta> %sNice try, you are still muted.", ChatColor.BLUE, ChatColor.GRAY));
        } else {
            String targetName = args[0];
            Player p = Bukkit.getPlayer(targetName);
            if (p == null) {
                sender.sendMessage(String.format("%sInvicta> %s%s is not an actual player!", ChatColor.BLUE, ChatColor.GRAY, targetName));
                return true;
            }

            String restOfMsg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            String msg = String.format("[%s -> %s] %s", sender.getName(), targetName, restOfMsg);
            p.sendMessage(msg);
            sender.sendMessage(msg);
        }
        return true;
    }
}
