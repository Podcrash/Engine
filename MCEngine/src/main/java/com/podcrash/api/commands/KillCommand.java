package com.podcrash.api.commands;

import com.podcrash.api.damage.DamageQueue;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class KillCommand extends BukkitCommand {

    public KillCommand() {
        super("kill",
                "Kills your player if you are currently in a game.",
                "kill",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length != 0) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (!GameManager.hasPlayer(player) || GameManager.getGame().getGameState() == GameState.LOBBY){
            sender.sendMessage(String.format("%sInvicta> %sYou must be in a game to use this command!", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        if (!GameManager.getGame().isSpectating(player)){
            if (GameManager.getGame().isRespawning(player)) {
                sender.sendMessage(String.format("%sInvicta> %sYou are already dead.", ChatColor.BLUE, ChatColor.GRAY));
                return true;
            }
            DamageQueue.artificialDie(player);
            return true;
        }
        return true;
    }
}
