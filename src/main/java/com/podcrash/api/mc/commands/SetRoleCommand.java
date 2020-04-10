package com.podcrash.api.mc.commands;

import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.db.TableOrganizer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetRoleCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage("Try using: /setrole [ROLE] [PLAYER]");
            return true;
        }
        OfflinePlayer arg2Player = getUUID(args[1]);
        UUID playerUUID = arg2Player.getUniqueId();
        if(playerUUID == null) {
            sender.sendMessage("Player " + args[1] + " has never joined this server before!");
            return true;
        }
        String newRole = args[0];

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        if(args.length == 2) {
            if(table.hasRoleSync(playerUUID, newRole)) {
                table.removeRole(playerUUID, newRole);

                sender.sendMessage("Successful removed " + args[1] + "'s role: " + newRole);
            }else{

                table.addRole(playerUUID, newRole);
                sender.sendMessage("Successfully added " + args[1] + "'s role: " + newRole);
            }
            Player p;
            if((p = Bukkit.getPlayer(playerUUID)) != null) {
                PodcrashSpigot.getInstance().setupPermissions(p);
            }
            return true;
        }
        return false;
    }

    /**
     * we have to use the uuid api to search via username
     * @param args
     * @return
     */
    public OfflinePlayer getUUID(String args) {
        if(Bukkit.getPlayer(args) != null) {
            return Bukkit.getPlayer(args);
        }else if (Bukkit.getOfflinePlayer(args).hasPlayedBefore()){
            return Bukkit.getOfflinePlayer(args);
        }
        return null;
    }
}