package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player) || args.length != 1) return false;
        Player p = (Player) sender;
        String item = args[0];
        EconomyHandler handler = (EconomyHandler) Pluginizer.getSpigotPlugin().getEconomyHandler();
        if(!handler.containsItem(item)) {
            p.sendMessage(item + " does not exist!");
        }
        handler.buy(p, item);
        return true;
    }
}
