package com.podcrash.api.mc.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SetMapCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.host")) {
            Player player = (Player) sender;
            MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
            Set<String> validMaps = new HashSet<>(table.getWorlds(GameManager.getGame().getMode()));
            if (args.length == 0) {
                sender.sendMessage("Require a mapname");
                sender.sendMessage("List of the available maps: " + validMaps.toString());
            } else if(args.length == 1){
                boolean valid = isValidMap(validMaps, args[0]);
                if (GameManager.hasPlayer(player) && valid) {
                    GameManager.setGameMap(args[0]);
                    player.sendMessage("You selected " + args[0]);
                } else if(!valid) {
                    player.sendMessage("That is not a valid map: available maps are " + validMaps.toString());
                }else player.sendMessage("You are currently not in a game");
            }
        } else {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }

    private boolean isValidMap(Set<String> validMaps, String mapName) {
        for(String map : validMaps) {
            if(mapName.equals(map)) {
                return true;
            }
        }
        return false;
    }
}
