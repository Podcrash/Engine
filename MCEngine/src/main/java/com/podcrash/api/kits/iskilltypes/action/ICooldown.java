package com.podcrash.api.kits.iskilltypes.action;

import com.podcrash.api.kits.KitPlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public interface ICooldown {
    String getName();
    Player getPlayer();

    float getCooldown();

    default boolean hasCooldown() {
        return getCooldown() != -1;
    }
    default boolean onCooldown() {
        return (System.currentTimeMillis() - getLastUsed()) < getCooldown() * 1000L;
    }
    default double cooldown() {
        return (getCooldown() - ((System.currentTimeMillis() - getLastUsed())) / 1000D);
    }

    long getLastUsed();


    default String getCooldownMessage() {
        return String.format(
                "%s%s> %s%s %scannot be used for %s%.2f %sseconds",
                ChatColor.BLUE,
                KitPlayerManager.getInstance().getKitPlayer(getPlayer()).getName(),
                ChatColor.GREEN,
                getName(),
                ChatColor.GRAY,
                ChatColor.GREEN,
                cooldown(),
                ChatColor.GRAY);
    }

    default String getCanUseMessage() {
        return String.format(
                "%sRecharge> %sYou can use %s%s%s.",
                ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }
}
