package com.podcrash.api.commands;

import com.podcrash.api.damage.HitDetectionInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class HitRegCommand extends BukkitCommand {

    public HitRegCommand() {
        super("hitreg",
                "Put a new hitreg.",
                "/hitreg <integer>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.testing")) {
            sender.sendMessage("You do not have permission!");
            return true;
        }
        if (args.length == 1) {
            try {
                HitDetectionInjector.delay = Long.parseLong(args[0]);
                sender.sendMessage("[HitReg] " + "Changed to " + args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("[HitReg] " + args[0] + " must be a number!");
            }
            return true;
        } else {
            sender.sendMessage("[HitReg] Please provide an argument!");
        }
        return false;
    }

}
