package com.podcrash.api.ui;

import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.util.ChatUtil;
import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Team Selection Menu.
 *
 * @author JJCunningCreeper
 */
public class TeamSelectGUI {

    public static String inventory_name = ChatUtil.chat("&0&lSelect Team");

    public static Inventory selectTeam(Game game, Player p) {
        inventory_name = ChatUtil.chat("&0&lSelect Team");
        List<ItemStack> teamIcons = new ArrayList<>();
        for (GTeam team : game.getTeams()) {
            String boldedColored = ChatColor.BOLD.toString() + team.getTeamEnum().getChatColor();
            ItemStack icon = ItemStackUtil.createItem(35, team.getTeamEnum().getData(), 1, boldedColored);
            if (game.getTeam(p) != null && game.getTeam(p).getTeamEnum().getData() == team.getTeamEnum().getData()) {
                ItemMeta meta = icon.getItemMeta();
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                icon.setItemMeta(meta);
            }
            teamIcons.add(icon);
        }
        return MenuCreator.createMenu(inventory_name, null, teamIcons, false);
    }
}
