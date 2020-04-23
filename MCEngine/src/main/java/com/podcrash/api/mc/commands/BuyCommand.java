package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class BuyCommand extends BukkitCommand {

    public BuyCommand() {
        super("buy",
                "Purchase a new item with your gold.",
                "/buy <Name of Item>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || args.length != 1)
            return false;
        Player p = (Player) sender;
        String item = args[0];
        EconomyHandler handler = PodcrashSpigot.getInstance().getEconomyHandler();
        if (!handler.containsItem(item)) {
            p.sendMessage(item + " does not exist!");
        }
        handler.buy(p, item);
        return true;
    }
}
