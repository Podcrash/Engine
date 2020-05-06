package com.podcrash.api.commands;

import com.podcrash.api.commands.helpers.PPLCommands;
import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class DecreaseMaxPlayersCommand extends BukkitCommand {
    public DecreaseMaxPlayersCommand() {
        super("decrease",
                "Decrease the maximum player count within a PPL",
                "/decrease",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("invicta.host")) return true;
        PPLCommands.decreaseMaxPlayers();
        return true;

    }
}
