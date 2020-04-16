package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.game.TeamEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TeamCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            // If the sender is spectating, remove them from the list of spectators before assigning to a team.
            if(GameManager.getGame().isSpectating((Player) sender)) GameManager.getGame().toggleSpec((Player) sender);

            Player player = (Player) sender;

            // Check to make sure the sender has included the proper arguments to use this command.
            if (args.length == 1) {
                String team = args[0];
                boolean valid = false;

                // Quickly make sure that there will not be a NullPointer error from the specified team not existing.
                for(TeamEnum teamEnum : GameManager.getGame().getTeamSettings().getTeamColors()) {
                    if(teamEnum.getName().equalsIgnoreCase(team)) {
                        valid = true;
                        break;
                    }
                }
                if(!valid) {
                    player.sendMessage(
                            String.format(
                                    "%sInvicta> %sThe team you specified does not exist.",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY));
                    return true;
                }

                // Check to make sure the game has not already started; if it has, do not allow the player to change teams.
                if (GameManager.getGame().getGameState() == GameState.LOBBY) {
                    GameManager.joinTeam(player, TeamEnum.getByColor(team));
                } else {
                    player.sendMessage(
                            String.format(
                                    "%sInvicta> %sYou may not switch teams mid-game!",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY));
                }
                return true;
            }
        }
        return false;
    }
}
