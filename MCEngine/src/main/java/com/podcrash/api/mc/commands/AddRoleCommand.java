package com.podcrash.api.mc.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.RanksTable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AddRoleCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage("invalid command: try /addrole [ROLE NAME] [COLOR] [POSITION]");
            return true;
        }

        String roleName = args[0];



        String color = args[1];
        int position = Integer.parseInt(args[2]);

        switch(color) {
            case "black":
                color = ChatColor.BLACK.toString();
                break;
            case "darkblue":
                color = ChatColor.DARK_BLUE.toString();
                break;
            case "darkgreen":
                color = ChatColor.DARK_GREEN.toString();
                break;
            case "darkaqua":
                color = ChatColor.DARK_AQUA.toString();
                break;
            case "darkred":
                color = ChatColor.DARK_RED.toString();
                break;
            case "darkpurple":
                color = ChatColor.DARK_PURPLE.toString();
                break;
            case "gold":
                color = ChatColor.GOLD.toString();
                break;
            case "gray":
                color = ChatColor.GRAY.toString();
                break;
            case "darkgray":
                color = ChatColor.DARK_GRAY.toString();
                break;
            case "blue":
                color = ChatColor.BLUE.toString();
                break;
            case "green":
                color = ChatColor.GREEN.toString();
                break;
            case "aqua":
                color = ChatColor.AQUA.toString();
                break;
            case "red":
                color = ChatColor.RED.toString();
                break;
            case "lightpurple":
                color = ChatColor.LIGHT_PURPLE.toString();
                break;
            case "yellow":
                color = ChatColor.YELLOW.toString();
                break;
            case "white":
                color = ChatColor.WHITE.toString();
                break;
        }

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        table.addRank(roleName, color, position);

        return false;

    }
}
