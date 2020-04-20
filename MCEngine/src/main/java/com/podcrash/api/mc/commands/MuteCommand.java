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

public class MuteCommand implements CommandExecutor {

    //TODO fix this

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("invicta.mod")) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        if(args.length == 1) {
            OfflinePlayer player = getUUID(args[0]);
            if(player == null) {
                sender.sendMessage("Player " + args[0] + " has never joined this server before!");
                return true;
            }
            UUID playerUUID = player.getUniqueId();
            RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
            table.hasRoleAsync(playerUUID, "MUTED").thenApply(hasRole-> {
                if(hasRole) {
                    table.removeRole(playerUUID, "MUTED");
                    sender.sendMessage("Successful unmuted " + args[0] + ".");
                } else {
                    table.addRole(playerUUID, "MUTED");
                    sender.sendMessage("Successful muted " + args[0] + ".");
                }
                Player p;
                if((p = Bukkit.getPlayer(playerUUID)) != null) {
                    PodcrashSpigot.getInstance().setupPermissions(p);
                }
                return true;
            });
        }
        return false;
    }

    private OfflinePlayer getUUID(String args) {
        if(Bukkit.getPlayer(args) != null) {
            return Bukkit.getPlayer(args);
        }else if (Bukkit.getOfflinePlayer(args).hasPlayedBefore()){
            return Bukkit.getOfflinePlayer(args);
        }
        return null;
    }
}
