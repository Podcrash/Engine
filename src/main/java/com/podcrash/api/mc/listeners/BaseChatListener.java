package com.podcrash.api.mc.listeners;

import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.mc.util.PrefixUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BaseChatListener extends ListenerBase {
    public BaseChatListener(JavaPlugin plugin) {
        super(plugin);
    }


    //this will happen first
    @EventHandler(priority = EventPriority.LOW)
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(player.hasPermission("invicta.mute")){
            e.setCancelled(true);
            player.sendMessage(String.format("%sConquest> %sYou are muted.", ChatColor.BLUE, ChatColor.GRAY));
            e.setCancelled(true);
            return;
        }

        String prefix = "";

        Rank rank = PrefixUtil.getPlayerRole(player);
        if(rank != null) {
            prefix = PrefixUtil.getPrefix(rank);
            prefix += " ";
        }

        e.setFormat(String.format("%s%s%s" + ChatColor.RESET + " %s%s",
                prefix,
                ChatColor.YELLOW,
                player.getName(),
                ChatColor.WHITE,
                e.getMessage())
        );
    }
}
