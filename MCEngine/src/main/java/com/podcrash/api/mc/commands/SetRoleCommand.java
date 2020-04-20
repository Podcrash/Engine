package com.podcrash.api.mc.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Try using: /setrole [ROLE] [PLAYER]");
            return true;
        }
        OfflinePlayer player = getOffline(args[1]);
        if (player == null) {
            sender.sendMessage("Player " + args[1] + " has never joined this server before!");
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        String newRole = args[0];

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        if (args.length != 2) {
            return false;
        }
        if (table.hasRoleSync(playerUUID, newRole)) {
            table.removeRole(playerUUID, newRole);
            sender.sendMessage("Successful removed " + args[1] + "'s role: " + newRole);
        } else {
            table.addRole(playerUUID, newRole);
            sender.sendMessage("Successfully added " + args[1] + "'s role: " + newRole);
        }
        Player p;
        if ((p = Bukkit.getPlayer(playerUUID)) != null) {
            PodcrashSpigot.getInstance().setupPermissions(p);
        }
        return true;
    }

    /**
     * Gets the bukkit offline player object for a given player name.
     * @param name The player name to search
     * @return The OfflinePlayer corresponding to the player name if it exists, or else null
     */
    public OfflinePlayer getOffline(String name) {
        if (Bukkit.getPlayer(name) != null) {
            return Bukkit.getPlayer(name);
        } else if (Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
            return Bukkit.getOfflinePlayer(name);
        }
        return null;
    }
}