package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.economy.Currency;
import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BalanceCommand extends BukkitCommand {

    public BalanceCommand() {
        super("balance",
                "Check how much gold you currently have.",
                "/balance",
                Collections.singletonList("bal"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        EconomyHandler handler = Pluginizer.getSpigotPlugin().getEconomyHandler();
        p.sendMessage(String.format("%sEconomy> %sYour %s: %s%s",
                ChatColor.BLUE, //Header
                ChatColor.GRAY, //Default color
                Currency.GOLD.getName(), //Currency name
                Currency.GOLD.getFormatting(),
                handler.getMoney(p)));
        return true;
    }
}

