package com.podcrash.api.mc.util;

import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.permissions.Perm;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class PrefixUtil {
    public static String getPrefix(Rank role){
        return role.getColor() + role.getName();
    }

    public static Rank getPlayerRole(Player player) {
        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        Set<Rank> currentRanks = table.getRanksSync(player.getUniqueId());
        if(currentRanks == null || currentRanks.size() == 0) return null;
        Rank output = null;
        for(Rank r : currentRanks) {
            if(output == null) output = r;
            else if (r.getPosition() < output.getPosition()) output = r;
        }
        /*
        Rank current = perms.get(0);
        for(int i = 1; i < perms.size(); i++) {
            if(current.getPosition() < perms.get(i).getPosition()) {
                current = perms.get(i);
            }
        }
         */
        return output;
    }
}
