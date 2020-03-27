package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.host") && args.length == 0) {
            Player player = (Player) sender;
            if (GameManager.hasPlayer(player)) {
                Game game = GameManager.getGame();
                if(game == null || game.isOngoing()) {
                    sender.sendMessage("Game has started already!");
                    return false;
                }
                log(game.toString());
                if (game.hasChosenMap()) {
                    GameManager.startGame();
                } else player.sendMessage("A map has not been set for Game #" + game.getId());
            } else player.sendMessage("You are currently not in a game");
        } else {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
