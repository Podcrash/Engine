package com.podcrash.api.listeners;

import com.podcrash.api.ui.MenuCreator;
import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class FriendsMenuListener extends ListenerBase {
    public FriendsMenuListener(JavaPlugin plugin) {super(plugin); }

    public static Inventory createFriendsMenu(Set<String> friendNames) {

        List<ItemStack> friendSkulls = new ArrayList<>();

        for (String name : friendNames) {
            boolean status = Bukkit.getOfflinePlayer(name).isOnline();

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(name);
            meta.setDisplayName(String.format("%s%s", ChatColor.GRAY, name));
            meta.setLore(Arrays.asList("Status: " + status, "", "second line test", "", "third line yas"));
            skull.setItemMeta(meta);

            friendSkulls.add(skull);
        }
        return MenuCreator.createMenuSimple("Friends", null, friendSkulls, true, true);
    }
}
