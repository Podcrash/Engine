package com.podcrash.api.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetMapCommand extends BukkitCommand {

    public SetMapCommand() {
        super("setmap",
                "Set a map for the game.",
                "/setmap <MapName>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("invicta.host")) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        Player player = (Player) sender;
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        Set<String> validMaps = new HashSet<>(table.getWorlds(GameManager.getGame().getMode()));
        if (args.length == 0) {
            sender.sendMessage("A map name is required.");
            sender.sendMessage("List of the available maps: " + validMaps.toString());
            return true;
        }
        if (args.length != 1) {
            return true;
        }
        boolean valid = isValidMap(validMaps, args[0]);
        if (GameManager.hasPlayer(player) && valid) {
            if (GameManager.getGame().getMapName().equalsIgnoreCase(args[0])) {
                sender.sendMessage("You already have that map selected.");
            } else {
                GameManager.setGameMap(args[0]);
                player.sendMessage("You selected " + args[0]);
            }
        } else if (!valid) {
            player.sendMessage("That is not a valid map: available maps are " + validMaps.toString());
        } else {
            player.sendMessage("You are currently not in a game.");
        }
        return true;
    }

    private boolean isValidMap(Set<String> validMaps, String mapName) {
        return validMaps.contains(mapName);
    }
}
