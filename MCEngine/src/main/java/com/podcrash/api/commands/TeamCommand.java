package com.podcrash.api.commands;

import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.game.TeamEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;


public class TeamCommand extends BukkitCommand {

    public TeamCommand() {
        super("team",
                "Join a team (red or blue).",
                "/team <color>",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        // If the sender is spectating, remove them from the list of spectators before assigning to a team.
        if (GameManager.getGame().isSpectating((Player) sender))
            GameManager.getGame().toggleSpec((Player) sender);

        Player player = (Player) sender;

        // Check to make sure the sender has included the proper arguments to use this command.
        if (args.length != 1) {
            return true;
        }
        String team = args[0];
        boolean valid = false;

        // Quickly make sure that there will not be a NPE from the specified team not existing.
        for(TeamEnum teamEnum : GameManager.getGame().getTeamSettings().getTeamColors()) {
            if (teamEnum.getName().equalsIgnoreCase(team)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            player.sendMessage(String.format("%sInvicta> %sThe team you specified does not exist.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }

        // Check to make sure the game has not already started; if it has, do not allow the player to change teams.
        if (GameManager.getGame().getGameState() == GameState.LOBBY) {
            GameManager.joinTeam(player, TeamEnum.getByColor(team));
        } else {
            player.sendMessage(String.format("%sInvicta> %sYou may not switch teams mid-game!", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
