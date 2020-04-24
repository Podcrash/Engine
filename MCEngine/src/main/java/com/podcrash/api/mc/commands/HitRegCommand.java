package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.damage.HitDetectionInjector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HitRegCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("invicta.testing")) {
            sender.sendMessage("You do not have permissions!");
            return false;
        }
        if(args.length == 1) {
            try {
                long newReg = Long.parseLong(args[0]);
                HitDetectionInjector.delay = newReg;
                sender.sendMessage("[HitReg] " + "Changed to " + args[0]);
            }catch (NumberFormatException e) {
                sender.sendMessage("[HitReg] "+ args[0] + " must be a number!");
            }
            return true;
        }else sender.sendMessage("[HitReg] Please provide an argument!");
        return false;
    }

}
