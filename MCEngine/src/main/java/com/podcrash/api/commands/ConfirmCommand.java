package com.podcrash.api.commands;

import com.podcrash.api.economy.EconomyHandler;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ConfirmCommand extends BukkitCommand {

    public ConfirmCommand() {
        super("confirm",
                "Confirm the purchase of an item.",
                "/confirm <Name of Item>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        if (args.length == 0) {
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
        EconomyHandler handler = PodcrashSpigot.getInstance().getEconomyHandler();
        handler.confirm(p, item);
        return true;
    }
}
