package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(!GameManager.hasPlayer(player) || !GameManager.getGame().isOngoing()){
                    sender.sendMessage(String.format("%sInvicta> %sYou must be in a game to use this command!", ChatColor.BLUE, ChatColor.GRAY));
                    return true;
                }
                if(!GameManager.getGame().isSpectating(player)){
                    if(GameManager.getGame().isRespawning(player)) {
                        sender.sendMessage(String.format("%sInvicta> %sYou are already dead.", ChatColor.BLUE, ChatColor.GRAY));
                        return true;
                    }
                    DamageQueue.artificialDie(player);
                    return true;
                }
            }
        }
        return false;
    }
}
