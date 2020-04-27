package com.podcrash.api.commands;

import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class SpecCommand extends BukkitCommand {

    public SpecCommand() {
        super("spectate",
                "Spectate a game.",
                "/spectate",
                Collections.singletonList("spec"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (GameManager.getGame().getGameState() == GameState.STARTED) {
            player.sendMessage(String.format("%sInvicta> %sYou may not switch teams mid-game!", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        GameManager.getGame().toggleSpec(player);
        return true;
    }
}
