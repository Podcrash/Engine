package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameLobbyTimer;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.host")) {
            if(args.length == 1 && args[0].equalsIgnoreCase("fast")) {
                GameManager.startGame();
            } else {
                Player player = (Player) sender;
                if (GameManager.hasPlayer(player)) {
                    Game game = GameManager.getGame();
                    if(game == null || game.getGameState() == GameState.STARTED) {
                        sender.sendMessage("Game has started already!");
                        return true;
                    }
                    GameLobbyTimer timer = game.getTimer();
                    if(timer.isRunning()) {
                        timer.stop(true);
                        player.sendMessage("Timer paused");
                        return true;
                    }
                    log(game.toString());
                    if (game.hasChosenMap()) {
                        timer.start();
                    } else player.sendMessage("A map has not been set for Game #" + game.getId());
                } else player.sendMessage("You are currently not in a game");
            }
        } else {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
