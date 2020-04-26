package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EndCommand extends BukkitCommand {

    public EndCommand() {
        super("endgame",
                "End a game.",
                "/endgame",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        } else {
            if (!(sender instanceof Player) || GameManager.getGame() == null)
                return true;
            if (GameManager.getGame().getGameState() == GameState.LOBBY) {
                if (!GameManager.getGame().getTimer().getStatus().equals(GameManager.getGame().getTimer().getDefaultStatus())) {
                    GameManager.getGame().getTimer().stop(false);
                    sender.sendMessage(String.format("%sInvicta> %sStopping the countdown.", ChatColor.BLUE, ChatColor.GRAY));
                    return true;
                }
                sender.sendMessage(String.format("%sInvicta> %sThe game has not started yet.", ChatColor.BLUE, ChatColor.GRAY));
                return true;
            }
            Game game = GameManager.getGame();
            GameManager.endGame(game);
            return true;
        }
        return true;
    }
}
