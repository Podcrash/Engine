package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecCommand extends CommandBase {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(GameManager.getGame().isOngoing()) {
                player.sendMessage(
                        String.format(
                                "%sInvicta> %sYou may not switch teams mid-game!",
                                ChatColor.BLUE,
                                ChatColor.GRAY));
                return true;
            }
            GameManager.getGame().toggleSpec(player);
            if(GameManager.getGame().isSpectating(player)) {
                sender.sendMessage(String.format(
                        "%sInvicta> %sYou joined the %sSpectators %sin %sGame %s%s.",
                        ChatColor.BLUE,
                        ChatColor.GRAY,
                        ChatColor.YELLOW,
                        ChatColor.GRAY,
                        ChatColor.GREEN,
                        GameManager.getGame().getId(),
                        ChatColor.GRAY));
            } else {
                GameManager.randomTeam(player);
            }
            return true;
        }
        return false;
    }
}
