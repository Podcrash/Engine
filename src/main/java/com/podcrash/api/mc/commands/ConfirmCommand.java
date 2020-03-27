package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfirmCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(args.length == 0) {
            sender.sendMessage(
                    String.format(
                            "%SEconomy> %sUse the command %s%s/confirm [NAME OF ITEM] %sto confirm your purchase!",
                            ChatColor.BLUE,
                            ChatColor.GRAY,
                            ChatColor.YELLOW,
                            ChatColor.BOLD,
                            ChatColor.GRAY));
            return true;
        }
        Player p = (Player) sender;
        String item = args[0];
        EconomyHandler handler = (EconomyHandler) Pluginizer.getSpigotPlugin().getEconomyHandler();
        handler.confirm(p, item);
        return true;
    }
}
