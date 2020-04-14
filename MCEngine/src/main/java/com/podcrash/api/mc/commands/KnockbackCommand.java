package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.commands.CommandBase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

public class KnockbackCommand extends CommandBase {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("invicta.testing")) {
            if(args.length == 0) {
                sender.sendMessage(getValues());
                sender.sendMessage(ChatColor.BLUE + "[Knockback] To change KB values: /kb kbfriction kbhorizontal kbextrahorizontal kbvertical kbverticallimit kbextravertical");
            }else if(args.length < 6) {
                if(args.length == 1 && args[0].equalsIgnoreCase("reset")) {
                    SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
                    SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
                    SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
                    SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
                    SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
                    SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Knockback reset to default values!");
                    sender.sendMessage(getValues());
                }else sender.sendMessage(ChatColor.BLUE + "[Knockback] Too little arguments!");
            }else if(args.length == 6) {
                try {
                    SpigotConfig.knockbackFriction = Double.parseDouble(args[0]);
                    SpigotConfig.knockbackHorizontal = Double.parseDouble(args[1]);
                    SpigotConfig.knockbackExtraHorizontal= Double.parseDouble(args[2]);
                    SpigotConfig.knockbackVertical = Double.parseDouble(args[3]);
                    SpigotConfig.knockbackVerticalLimit = Double.parseDouble(args[4]);
                    SpigotConfig.knockbackExtraVertical = Double.parseDouble(args[5]);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Knockback Changed!");
                    sender.sendMessage(getValues());
                }catch (NumberFormatException e) {
                   sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[Knockback] one of the arguments is not a decimal!");
                }
            }else {
                sender.sendMessage(ChatColor.BLUE + "[Knockback] Too many arguments!");
            }
        }
        return true;
    }

    private String getValues() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.DARK_GREEN);
        builder.append("[Knockback] Current Knockback:\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Friction: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackFriction);
        builder.append("\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Horizontal: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackHorizontal);
        builder.append("\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Extra Horizontal: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackExtraHorizontal);
        builder.append("\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Vertical: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackVertical);
        builder.append("\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Vertical Limit: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackVerticalLimit);
        builder.append("\n");

        builder.append(ChatColor.GREEN);
        builder.append("Knockback Extra Vertical: " );
        builder.append(ChatColor.YELLOW);
        builder.append(SpigotConfig.knockbackExtraVertical);
        builder.append("\n");

        return builder.toString();
    }
}
