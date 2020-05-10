package com.podcrash.api.commands;

import com.podcrash.api.commands.helpers.PPLCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * simple whitelist command using the bukkit system
 * we might want to hold our own instance of the list just because
 */
public class WhitelistCommand extends BukkitCommand {

    public WhitelistCommand() {
        super("whitelist",
                "Whitelist players in a PPL!",
                "/whitelist <user>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) return true;

        if (args.length == 0) {
            PPLCommands.toggleWhitelist();
            String msg = String.format("%sPPL Menu> %s%s",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    PPLCommands.getStateMsg()
            );
            sender.sendMessage(msg);
        }else if (args.length == 1) {
            String arg = args[0].toLowerCase();
            Set<String> currentlyWhitelisted = convertToNames(Bukkit.getWhitelistedPlayers());

            if (arg.equalsIgnoreCase("list")) {
                StringBuilder builder = new StringBuilder();
                for (String player : currentlyWhitelisted) {
                    builder.append(player);
                    builder.append(' ');
                }
                sender.sendMessage("List of players whitelisted: " + builder.toString());
                return true;
            }
            //if it is not "list".
            PPLCommands.whitelist(arg, !currentlyWhitelisted.contains(arg));
            sender.sendMessage(String.format("%sPPL Menu> %s%s %s%s",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    (currentlyWhitelisted.contains(arg) ? "Un-Whitelisted:" : "Whitelisted:"),
                    ChatColor.YELLOW,
                    arg
            ));

        }

        return true;
    }

    private Set<String> convertToNames(Set<OfflinePlayer> players) {
        Set<String> names = new LinkedHashSet<>();
        for (OfflinePlayer player : players)
            names.add(player.getName().toLowerCase());
        return names;
    }

}
