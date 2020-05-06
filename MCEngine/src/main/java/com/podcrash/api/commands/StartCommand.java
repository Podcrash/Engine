package com.podcrash.api.commands;

import com.podcrash.api.commands.helpers.GameCommands;
import com.podcrash.api.commands.helpers.PPLCommands;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.lobby.GameLobbyTimer;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StartCommand extends BukkitCommand {

    public StartCommand() {
        super("startgame",
                "Start a game.",
                "/startgame",
                Collections.singletonList("start"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("invicta.host")) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }

        GameCommands.startGame((Player) sender, args.length == 1 && args[0].equalsIgnoreCase("fast"));
        return true;
    }

    private void log(String msg){
        PodcrashSpigot.getInstance().getLogger().info(String.format("%s %s", this.getClass().getSimpleName(), msg));
    }

}
