package com.podcrash.api.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.listeners.FriendsMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** TODO send request instead of directly adding
 *  TODO gui alternative at some point?
 *  TODO use updateresult instead of contains(name) to save up on queries
 */

public class FriendCommand extends BukkitCommand {
    public FriendCommand() {
        super("f",
                "Add or remove friends.",
                "/f <remove> (Player Name)",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        Player sender = (Player) commandSender;
        PlayerTable table = TableOrganizer.getTable(DataTableType.PLAYERS);

        if(strings.length == 0) {
            showFriendList(sender, table);
            sender.openInventory(FriendsMenuListener.createFriendsMenu());
        } else if (strings.length == 1) {
            addFriendToPlayer(sender, strings[0], table);
        } else if (strings.length == 2){
            switch (strings[0]) {
                case "remove": {
                    removeFriendFromPlayer(sender, strings[1], table);
                    break;
                }
                default: sender.sendMessage(String.format("%s%s%s is not a valid argument", ChatColor.RED, ChatColor.ITALIC, strings[0]));
            }
        } else {
            commandSender.sendMessage(String.format("%sFriends> %sYou entered too many arguments!",ChatColor.BLUE ,ChatColor.GRAY));
        }
        return true;
    }

    private void showFriendList(Player recipient, PlayerTable table) {
        table.getFriendsAsync(recipient.getUniqueId()).thenApply((friends) -> {
            if(friends != null && friends.size() > 0) recipient.sendMessage(getNiceFriendList(friends));
            else recipient.sendMessage(ChatColor.RED + "No friends added.");
            return true;
        });
    }

    private void removeFriendFromPlayer(Player sender, String targetIGN, PlayerTable table) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetIGN);
        if(target == null || target.getName().equals(sender.getName())) return;

        table.hasFriendAsync(target.getUniqueId(), sender.getUniqueId()).thenApply((hasFriend) -> {
            if (hasFriend) {
                table.removeFriend(target.getUniqueId(), sender.getUniqueId());
                sender.sendMessage(String.format("%sFriends> %s%s %sis no longer your friend!",
                        ChatColor.BLUE, ChatColor.YELLOW, target.getName(), ChatColor.GRAY));
            } else {
                sender.sendMessage(String.format("%sFriends> %s%s was not found on your friend list!",
                        ChatColor.BLUE, ChatColor.GRAY, target.getName()));
            }
            return true;
        });

    }

    private void addFriendToPlayer(Player sender, String targetIGN, PlayerTable table) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetIGN);
        if(target == null || target.getName().equals(sender.getName())) return;

        table.hasFriendAsync(target.getUniqueId(), sender.getUniqueId()).thenApply((hasFriend) -> {
            if (hasFriend) {
                sender.sendMessage(String.format("%sFriends> %sYou are already friends with %s%s%s!",
                        ChatColor.BLUE, ChatColor.GRAY, ChatColor.YELLOW, target.getName(), ChatColor.GRAY));
            } else {
                table.addFriend(target.getUniqueId(), sender.getUniqueId());
                sender.sendMessage(String.format("%sFriends> %s%s %sis now your friend!",
                        ChatColor.BLUE, ChatColor.YELLOW, target.getName(), ChatColor.GRAY));
            }
            return true;
        });
    }

    private String getNiceFriendList(Set<UUID> uuids) {
        Set<OfflinePlayer> friends = new HashSet<>();
        for(UUID uuid : uuids) {
            friends.add(Bukkit.getOfflinePlayer(uuid));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        for(OfflinePlayer player : friends) {
            builder.append(player.getName()).append(" ");
        }
        builder.append("]");

        return builder.toString();
    }
}
