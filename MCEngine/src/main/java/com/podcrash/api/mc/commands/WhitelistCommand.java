package com.podcrash.api.mc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * simple whitelist command using the bukkit system
 * we might want to hold our own instance of the list just because
 */
public class WhitelistCommand extends CommandBase {
    //by default, mps systems will have no wls
    private boolean whitelistOn = false;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) return true;

        if (args.length == 0) {
            whitelistOn = !whitelistOn;
            Bukkit.setWhitelist(whitelistOn);
            sender.sendMessage(ChatColor.GRAY + "Whitelist: " + getStateMsg());
        }else if (args.length == 1) {
            String arg = args[0];
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
            if (currentlyWhitelisted.contains(arg)) {
                Bukkit.getOfflinePlayer(arg).setWhitelisted(false);
                sender.sendMessage("Un-Whitelisted " + arg);

                Player currentPlayer = Bukkit.getPlayer(arg);
                if (currentPlayer != null && !currentPlayer.hasPermission("invicta.exempt"))
                    currentPlayer.kickPlayer("Unwhitelisted!");

            } else {
                Bukkit.getOfflinePlayer(arg).setWhitelisted(true);
                sender.sendMessage("Whitelisted " + arg);

            }

            Bukkit.reloadWhitelist();

        }

        return true;
    }

    private Set<String> convertToNames(Set<OfflinePlayer> players) {
        Set<String> names = new LinkedHashSet<>();
        for (OfflinePlayer player : players)
            names.add(player.getName());
        return names;
    }

    private String getStateMsg() {
        return ChatColor.RESET + (whitelistOn ? ChatColor.GOLD + "On" : ChatColor.RED + "Off");
    }
}
