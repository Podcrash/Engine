package com.podcrash.api.mc.util;

import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.PlayerPermissionsTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.permissions.Perm;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class PrefixUtil {
    public static String getPrefix(Perm role){
        StringBuilder prefix = new StringBuilder();
        //IMPORTANT: update this method when you add a new role
        if(role == null) return "";
        switch(role) {
            case DEVELOPER:
                prefix.append(ChatColor.RED);
                prefix.append(ChatColor.BOLD);
                prefix.append("DEV ");
                break;
            case HOST:
                prefix.append(ChatColor.GREEN);
                prefix.append(ChatColor.BOLD);
                prefix.append("HOST ");
                break;
            default: return "";
        }

        return prefix.toString();
    }

    public static Perm getPlayerRole(Player player) {
        PlayerPermissionsTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        List<Perm> perms = table.getRoles(player.getUniqueId());
        for(Perm perm : Perm.values()) {
            if(perms.contains(perm)) {
                return perm;
            }
        }
        return null;
    }
}
