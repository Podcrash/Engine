package com.podcrash.api.commands;

import com.google.gson.JsonObject;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.MapTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.location.Coordinate;
import com.podcrash.api.mc.map.MapManager;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: REPLACE WITH ANNOTATIONS!
//TODO: REPLACE WITH SOMEHTING MORE SUITABLE
public class WorldCommand implements CommandExecutor {
    private String name;
    public WorldCommand() {
        this.name = "pworld";
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!commandSender.hasPermission("champions.developer")) {
            commandSender.sendMessage("You don't have permissions to run this command!");
            return false;
        }
        if(args.length <= 1) {
            StringBuilder builder = new StringBuilder("Command: /pworld\n");
            builder.append("info <name> = find information of the world from the db\n");
            builder.append("download <name> = download the world from the db\n");
            builder.append("upload <name> = upload the world to the db\n");
            builder.append("metadata <name> = save cache to db\n");
            builder.append("delete <name> = delete the world off the bukkit system\n");
            builder.append("infocache <name> = find information of the world from the cache\n");
            builder.append("set <name> <tag> <value> = Set the tag of the world\n");
            builder.append("locset <name> <tag> = Set the tag of the world to the value of your current position\n");
            commandSender.sendMessage(builder.toString());
            return true;
        }
        String worldName = args[1];
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        if(args[0].equalsIgnoreCase("info")) {
            table.findWorld(worldName, jsonObject -> {
                if(jsonObject == null) commandSender.sendMessage("This map doesn't exist yet!");
                else commandSender.sendMessage(jsonObject.toString());
            });
        }else if(args[0].equalsIgnoreCase("download")) {
            commandSender.sendMessage("Downloading... " + worldName);
            table.downloadWorld(worldName);
        }else if(args[0].equalsIgnoreCase("upload")) {
            commandSender.sendMessage("Uploading... " + worldName);
            table.uploadWorld(worldName).thenAccept((v) -> {
                commandSender.sendMessage("Finished uploading " + worldName + "!");
            });
        }else if(args[0].equalsIgnoreCase("metadata")) {
            commandSender.sendMessage("Sending metadata to db... " + worldName);
            try {
                MapManager.save(worldName);
                commandSender.sendMessage("Saved " + worldName + " metadata successfully from the cache!");
            } catch (IllegalAccessException e) {
                commandSender.sendMessage(worldName + " doesn't exist in the map setup!");
            }
        }else if(args[0].equalsIgnoreCase("delete")) {
            commandSender.sendMessage("Deleting world... " + worldName);
            WorldManager.getInstance().deleteWorld(Bukkit.getWorld(worldName), true);
        }else if(args[0].equalsIgnoreCase("infocache")) {
            ChatUtil.sendMessage(commandSender, MapManager.getFriendlyInfo(worldName));
        }else if(args[0].equalsIgnoreCase("set")) {
            if(args.length >= 5) {
                commandSender.sendMessage("This command only works with 4 arguments!");
                return true;
            }
            String tag = args[2];
            String value = args[3];
            MapManager.insert(worldName, tag, value);
            commandSender.sendMessage(String.format("Setted tag %s to a value of %s in world %s", tag, value, worldName));
        }else if(args[0].equalsIgnoreCase("mlocset")) {
            if(args.length != 4 || !(commandSender instanceof Player)) {
                commandSender.sendMessage("This command only works with 4 arguments and if the sender was a player!");
                return true;
            }
            int index;
            try {
               index = Integer.parseInt(args[3]);
            }catch (NumberFormatException e) {
                commandSender.sendMessage(args[3] + " is not a number!");
                return true;
            }
            String tag = args[2];
            MapManager.insert(worldName, tag, index, Coordinate.fromLocation(((Player) commandSender).getLocation()));
            commandSender.sendMessage(String.format("Setted tag %s of index %d to a value of %s in world %s", tag, index, ((Player) commandSender).getLocation(), worldName));
        }else if(args[0].equalsIgnoreCase("locset")) {
            if(args.length != 3 || !(commandSender instanceof Player)) {
                commandSender.sendMessage("This command only works with 3 arguments and if the sender was a player!");
                return true;
            }
            String tag = args[2];
            MapManager.insert(worldName, tag, Coordinate.fromLocation(((Player) commandSender).getLocation()));
            commandSender.sendMessage(String.format("Setted tag %s to a value of %s in world %s", tag, ((Player) commandSender).getLocation(), worldName));
        }
        return true;
    }
}
