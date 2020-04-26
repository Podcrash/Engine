package com.podcrash.api.commands;

import com.podcrash.api.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PingCommand extends BukkitCommand {

    public PingCommand() {
        super("ping",
                "Displays you or another player's ping.",
                "/ping (Player Name)",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(String.format("%sInvicta> %sYour ping: %s%d", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, Utility.ping(player)));
        } else if (args.length == 1) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                player.sendMessage(String.format("%sInvicta> %sCannot find %s%s%s, are they online?", ChatColor.BLUE, ChatColor.GRAY, ChatColor.YELLOW, args[0], ChatColor.GRAY));
                return true;
            } else {
                player.sendMessage(String.format("%sInvicta> %s%s%s's ping: %s%d", ChatColor.BLUE, ChatColor.YELLOW, p.getName(), ChatColor.GRAY, ChatColor.GREEN, Utility.ping(p)));
            }
        }
        return true;
    }
}
