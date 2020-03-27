package com.podcrash.api.mc.commands;

import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Use our own command executor to do cooler things
 */
public abstract class CommandBase implements CommandExecutor {
    private final JavaPlugin plugin;

    public CommandBase() {
        plugin = PodcrashSpigot.getInstance();
        plugin.getLogger().info(String.format("[Commands] Registering %s", this.getClass().getSimpleName()));
    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

    protected void log(String msg){
        plugin.getLogger().info(String.format("%s %s", this.getClass().getSimpleName(), msg));
    }
}
