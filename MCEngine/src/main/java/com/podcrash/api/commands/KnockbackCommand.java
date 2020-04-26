package com.podcrash.api.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.Collections;

public class KnockbackCommand extends BukkitCommand {

    public KnockbackCommand() {
        super("knockback",
                "Modify the KB values.",
                "/kb ...",
                Collections.singletonList("kb"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("invicta.testing")) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(getValues());
            sender.sendMessage(ChatColor.BLUE + "[Knockback] To change KB values: /kb <friction> <horizontal> <extrahorizontal> <vertical> <verticallimit> <extravertical>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
            SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
            SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
            SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
            SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
            SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Knockback reset to default values!");
            sender.sendMessage(getValues());
            return true;
        }

        if (args.length != 6) {
            sender.sendMessage(ChatColor.BLUE + "[Knockback] Invalid arguments!");
            return false;
        }
        try {
            SpigotConfig.knockbackFriction = Double.parseDouble(args[0]);
            SpigotConfig.knockbackHorizontal = Double.parseDouble(args[1]);
            SpigotConfig.knockbackExtraHorizontal= Double.parseDouble(args[2]);
            SpigotConfig.knockbackVertical = Double.parseDouble(args[3]);
            SpigotConfig.knockbackVerticalLimit = Double.parseDouble(args[4]);
            SpigotConfig.knockbackExtraVertical = Double.parseDouble(args[5]);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Knockback Changed!");
            sender.sendMessage(getValues());
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[Knockback] one of the arguments is not a decimal!");
            return false;
        }
    }

    private String getValues() {

        return ChatColor.DARK_GREEN +
                "[Knockback] Current Knockback:\n" +
                ChatColor.GREEN +
                "Knockback Friction: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackFriction +
                "\n" +
                ChatColor.GREEN +
                "Knockback Horizontal: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackHorizontal +
                "\n" +
                ChatColor.GREEN +
                "Knockback Extra Horizontal: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackExtraHorizontal +
                "\n" +
                ChatColor.GREEN +
                "Knockback Vertical: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackVertical +
                "\n" +
                ChatColor.GREEN +
                "Knockback Vertical Limit: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackVerticalLimit +
                "\n" +
                ChatColor.GREEN +
                "Knockback Extra Vertical: " +
                ChatColor.YELLOW +
                SpigotConfig.knockbackExtraVertical +
                "\n";
    }
}
