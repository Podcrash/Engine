package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.listeners.BackfillListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            if (BackfillListener.replaceOfflineWithSpectator((Player) sender, args[0]))
                sender.sendMessage("sucess");
            else
                sender.sendMessage("failure");
        }
        return true;
    }
}
