package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CmdPreprocessHandler extends ListenerBase {
    private Set<String> blockedCommands;
    public CmdPreprocessHandler(JavaPlugin plugin) {
        super(plugin);
        blockedCommands = new HashSet<>(
            Arrays.asList("/stop", "/me", "/say", "/help", "/plugins", "/pl", "/?"));
    }


    @EventHandler
    public void overrideStop(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        if(blockedCommands.contains(cmd))
            event.setCancelled(true);
    }
}
